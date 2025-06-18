package fr.dauphine.miageIf.minh.yang.trip_service.service;

import feign.FeignException;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripAccommodationDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripActivityDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripDayDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.*;
import fr.dauphine.miageIf.minh.yang.trip_service.exceptions.AmbiguousNameException;
import fr.dauphine.miageIf.minh.yang.trip_service.exceptions.BadRequestException;
import fr.dauphine.miageIf.minh.yang.trip_service.exceptions.NotFoundException;
import fr.dauphine.miageIf.minh.yang.trip_service.feign.InfoClient;
import fr.dauphine.miageIf.minh.yang.trip_service.model.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TripService {
    private final TripDao tripDao;
    private final TripDayDao tripDayDao;
    private final TripActivityDao tripActivityDao;
    private final TripAccommodationDao tripAccommodationDao;
    private final InfoClient infoClient;
    private static final Logger log = LoggerFactory.getLogger(TripService.class);
    //private final RouteClient routeClient;

    @Transactional
    public TripDetail createTrip(TripRequestDto req) {
        //1. 校验日期范围
        long totalDays = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
        if (req.getDays().size() != totalDays) {
            throw new BadRequestException("days list must match date range");
        }

        // 2. 使用无参构造 + setter 来保证每个字段都被写到实体里
        Trip trip = new Trip();
        trip.setName(req.getName());
        trip.setStartDate(req.getStartDate());
        trip.setEndDate(req.getEndDate());

        // 3. 保存主表
        trip = tripDao.save(trip);

        // 4. 继续插入 day / accommodation / activities...
        for (int i = 0; i < totalDays; i++) {
            int day = i + 1;
            TripRequestDto.TripDayInput in = req.getDays().get(i);

            // —— trip_day ——
            CityDto city = findUniqueCity(in.getCityName());
            TripDay dayEntity = new TripDay();
            TripDayKey dayKey = new TripDayKey(trip.getId(), day);
            dayEntity.setId(dayKey);
            dayEntity.setTrip(trip);
            dayEntity.setCityId(city.getId());
            tripDayDao.save(dayEntity);

            // —— trip_accommodation ——
            AccommodationDto acc = findUniqueAccommodationByName(city.getId(), in.getAccommodationName());
            TripAccommodation ta = new TripAccommodation();
            TripAccommodationKey accKey = new TripAccommodationKey(trip.getId(), day);
            ta.setId(accKey);
            ta.setTrip(trip);
            ta.setAccommodationId(acc.getId());
            tripAccommodationDao.save(ta);

            // —— trip_activity ——
            int seq = 1;
            for (String actName : in.getActivityNames()) {
                ActivityDto activity = findUniqueActivity(in.getCityName(), city.getId(), actName);
                TripActivity tapp = new TripActivity();
                TripActivityKey actKey = new TripActivityKey(trip.getId(), day, seq++);
                tapp.setId(actKey);
                tapp.setTrip(trip);
                tapp.setActivityId(activity.getId());
                tripActivityDao.save(tapp);
            }
        }

        // 5. 返回完整的 DTO
        return getTrip(trip.getId());
    }

   /* @Transactional
    public TripDetail updateTrip(Long tripId, TripRequestDto req) {
        Trip existing = tripDao.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));
        existing.setName(req.getName());
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());
        tripDao.save(existing);
        // 清除旧记录
        tripDayDao.deleteAll(tripDayDao.findByTrip_Id(tripId));
        tripActivityDao.deleteAll(tripActivityDao.findByTrip_Id(tripId));
        tripAccommodationDao.deleteAll(tripAccommodationDao.findByTrip_Id(tripId));
        // 重建
        return createTrip(req);
    }*/

    @Transactional
    public TripDetail updateTrip(Long tripId, TripRequestDto req) {
        tripDao.findById(tripId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        deleteTrip(tripId);
        req.setStartDate(req.getStartDate()); // reuse create logic
        req.setEndDate(req.getEndDate());
        return createTrip(req);
    }

    @Transactional
    public void deleteTrip(Long tripId) {
        if (!tripDao.existsById(tripId)) throw new NotFoundException("Trip not found: " + tripId);
        tripDao.deleteById(tripId);
    }

    /*@Transactional(readOnly = true)
    public TripDetail getTrip(Long tripId) {
        Trip trip = tripDao.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));
        // 构建详细 DTO
        List<TripDay> days = tripDayDao.findByTrip_IdOrderById_Day(tripId);
        TripDetail detail = new TripDetail();
        detail.setId(trip.getId());
        detail.setName(trip.getName());
        detail.setStartDate(trip.getStartDate());
        detail.setEndDate(trip.getEndDate());
        List<TripDetail.TripDayDto> dayDtos = new ArrayList<>();
        for (int i = 0; i < days.size(); i++) {
            TripDay td = days.get(i);
            CityDto city = infoClient.getCityById(td.getCityId());
            TripDetail.TripDayDto dto = new TripDetail.TripDayDto();
            dto.setDay(td.getId().getDay());
            dto.setCityName(city.getName());
            dto.setAccommodationName(infoClient.getAccommodationById(
                    tripAccommodationDao.findByTrip_IdAndId_Day(tripId, td.getId().getDay())
                            .orElseThrow(() -> new NotFoundException("Accommodation missing for day"+td.getId().getDay()))
                            .getAccommodationId()).getName());
            List<String> acts = tripActivityDao.findByTrip_IdAndId_Day(tripId, td.getId().getDay()).stream()
                    .map(ta -> infoClient.getActivityById(ta.getActivityId()).getName())
                    .collect(Collectors.toList());
            dto.setActivityNames(acts);
            if (i < days.size() - 1) {
                TripDay next = days.get(i+1);
                //todo
                //var dist = routeClient.getDistance(td.getCityId(), next.getCityId());
                //dto.setToNext(new TripDetail.ToNext(dist.getDistanceKm(), dist.getTravelTimeMin()));
            }
            dayDtos.add(dto);
        }
        detail.setDays(dayDtos);
        return detail;
    }*/
    @Transactional(readOnly = true)
    public TripDetail getTrip(Long tripId) {
        Trip trip = tripDao.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));
        List<TripDay> days = tripDayDao.findByTrip_IdOrderById_Day(tripId);
        List<TripDetail.TripDayDto> dtos = new ArrayList<>();
        for (int i = 0; i < days.size(); i++) {
            TripDay td = days.get(i);
            CityDto city = infoClient.getCityById(td.getCityId());
            String accName = infoClient.getAccommodationById(
                    tripAccommodationDao.findByTrip_IdAndId_Day(tripId, td.getId().getDay())
                            .orElseThrow(() -> new NotFoundException("Accommodation missing for day"+td.getId().getDay()))
                            .getAccommodationId()).getName();
            List<String> activityNames = tripActivityDao.findByTrip_IdAndId_Day(tripId, td.getId().getDay())
                    .stream().map(ta -> infoClient.getActivityById(ta.getActivityId()).getName())
                    .collect(Collectors.toList());
            //Todo
            //TripDetail.ToNext toNext = null;
            if (i < days.size() - 1) {
                String from = td.getCityId();
                String to   = days.get(i+1).getCityId();
             //   ToNextDto tn = routeClient.getDistance(from, to);
             //   toNext = new TripDetail.ToNext(tn.getDistanceKm(), tn.getTravelTimeMin());
            }
            //dtos.add(new TripDetail.TripDayDto(td.getId().getDay(), city.getName(), accName, activityNames, toNext));
            dtos.add(new TripDetail.TripDayDto(td.getId().getDay(), city.getName(), accName, activityNames));
        }
        return new TripDetail(trip.getId(), trip.getName(), trip.getStartDate(), trip.getEndDate(), dtos);

    }

    @Transactional(readOnly = true)
    public List<TripSummary> searchTrips(Integer minDays, Integer maxDays) {
        return tripDao.findAll().stream()
                .map(t -> new TripSummary(t.getId(),t.getName(),t.getStartDate(),t.getEndDate(),
                        (int)(ChronoUnit.DAYS.between(t.getStartDate(),t.getEndDate())+1)))
                .filter(s -> (minDays==null||s.getDayCount()>=minDays) && (maxDays==null||s.getDayCount()<=maxDays))
                .collect(Collectors.toList());
    }

    /*@Transactional(readOnly = true)
    public TripPointsOfInterestDto getPointsOfInterest(Long tripId) {
        Set<String> actNames = tripActivityDao.findByTrip_Id(tripId).stream()
                .map(ta -> infoClient.getActivityById(ta.getActivityId()).getName())
                .collect(Collectors.toSet());
        Set<String> accNames = tripAccommodationDao.findByTrip_Id(tripId).stream()
                .map(tac -> infoClient.getAccommodationById(tac.getAccommodationId()).getName())
                .collect(Collectors.toSet());
        // POI names via activities
        Set<String> poiNames = tripActivityDao.findByTrip_Id(tripId).stream()
                .map(ta -> infoClient.getActivityById(ta.getActivityId()).getPointOfInterest().getName())
                .collect(Collectors.toSet());
        return new TripPointsOfInterestDto(actNames, accNames, poiNames);
    }*/

    @Transactional(readOnly = true)
    public TripPointsOfInterestDto getPointsOfInterest(Long tripId) {
        // 1) 所有 TripActivity → 取 activityId → 调用 getActivityById → 收集活动名称
        Set<String> activityNames = tripActivityDao.findByTrip_Id(tripId).stream()
                .map(TripActivity::getActivityId)
                .map(infoClient::getActivityById)
                .map(ActivityDto::getName)
                .collect(Collectors.toSet());

        // 2) 所有 TripAccommodation → 取 accommodationId → 调用 getAccommodationById → 收集住宿名称
        Set<String> accommodationNames = tripAccommodationDao.findByTrip_Id(tripId).stream()
                .map(TripAccommodation::getAccommodationId)
                .map(infoClient::getAccommodationById)
                .map(AccommodationDto::getName)
                .collect(Collectors.toSet());

        // 3) 所有 TripActivity → 取 activityId → getActivityById → 取 pointOfInterestId → getPoiById → 收集 POI 名称
        Set<String> poiNames = tripActivityDao.findByTrip_Id(tripId).stream()
                .map(TripActivity::getActivityId)
                .map(infoClient::getActivityById)
                .map(ActivityDto::getPointOfInterestId)
                .map(infoClient::getPoiById)
                .map(PointOfInterestDto::getName)
                .collect(Collectors.toSet());

        return new TripPointsOfInterestDto(activityNames, accommodationNames, poiNames);
    }

    /**
     * 按名称查找且唯一返回 CityDto
     */
    private CityDto findUniqueCity(String name) {
        try {
            return infoClient.getCityByName(name);
        } catch (FeignException.NotFound ex) {
            throw new NotFoundException("City not found: " + name);
        }
    }

    /**
     * 在指定城市查找唯一的 AccommodationDto
     */
    private AccommodationDto findUniqueAccommodationByName(String cityId, String accommodationName) {
        List<AccommodationDto> accs = infoClient.getAccommodationsByCityId(cityId).stream()
                .filter(a -> a.getName().trim().equalsIgnoreCase(accommodationName.trim()))
                .toList();

        if (accs.isEmpty()) {
            throw new NotFoundException("Accommodation not found: " + accommodationName);
        }
        if (accs.size() > 1) {
            throw new AmbiguousNameException("Ambiguous accommodation: " + accommodationName);
        }
        return accs.get(0);
    }

    /**
     * 在指定城市查找唯一的 ActivityDto
     */
    private ActivityDto findUniqueActivity(String cityName, String cityId, String activityName){
        // 先按城市名称端点过滤
        List<ActivityDto> acts = infoClient.getActivitiesByCityName(cityName).stream()
                .filter(a -> a.getName().equalsIgnoreCase(activityName))
                .collect(Collectors.toList());

        if (acts.isEmpty()) {
            throw new NotFoundException("Activity not found: " + activityName);
        }
        if (acts.size() > 1) {
            throw new AmbiguousNameException("Ambiguous activity: " + activityName);
        }
        return acts.get(0);

    }

    /*private ActivityDto findUniqueActivity2(String cityName, String cityId, String activityName) {
        // 先按城市名称端点过滤
        List<ActivityDto> acts = infoClient.getActivitiesByCityName>>(cityName).stream()
                .filter(a -> a.getName().equalsIgnoreCase(activityName))
                .collect(Collectors.toList());

        // 回退到按 POI ID 批量查询
        if (acts.isEmpty()) {
            List<String> poiIds = fetchPoiIds(cityName, cityId);
            acts = infoClient.getActivitiesByPoiIds(poiIds).stream()
                    .filter(a -> a.getName().equalsIgnoreCase(activityName))
                    .collect(Collectors.toList());
        }
        if (acts.isEmpty()) {
            throw new NotFoundException("Activity not found: " + activityName);
        }
        if (acts.size() > 1) {
            throw new AmbiguousNameException("Ambiguous activity: " + activityName);
        }
        return acts.get(0);
    }*/

    /**
     * 通过城市名称获取该城市所有 POI，再筛出 ID 列表

    private List<String> fetchPoiIds(String cityName, String cityId) {
        List<PointOfInterestDto> pois = infoClient.getPoisByCityName(cityName);
        if (pois.isEmpty()) {
            pois = infoClient.getPoisByCityId(cityId);
        }
        return pois.stream()
                .map(PointOfInterestDto::getId)
                .collect(Collectors.toList());
    }*/

}
