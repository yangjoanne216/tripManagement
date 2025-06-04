package fr.dauphine.miageIf.minh.yang.trip_service.service;

import feign.FeignException;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripAccommodationDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripActivityDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.*;
import fr.dauphine.miageIf.minh.yang.trip_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.trip_service.feign.TripInterface;
import fr.dauphine.miageIf.minh.yang.trip_service.model.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TripService {
    private final TripDao tripDao;
    private final TripInterface tripInterface;
    //private final TripActivityDao tripActivityDao;
    //private final TripAccommodationDao tripAccommodationDao;

    /**
     * 创建一个新的 Trip，并连同 TripActivity、TripAccommodation 一并入库
     */
    @Transactional
    public TripDetail createTrip(TripRequestDto requestDto) {
        try {
          City fromCity = tripInterface.getCityById(requestDto.getStartCity()).getBody();
            // if no exception, city exists
        } catch (FeignException.NotFound nf) {
            throw new ResourceNotFoundException("Start city not found: " + requestDto.getStartCity());
        } catch (FeignException fe) {
            throw new RuntimeException("Route service unreachable", fe);
        }

        try {
            City toCity = tripInterface.getCityById(requestDto.getEndCity()).getBody();
        } catch (FeignException.NotFound nf) {
            throw new ResourceNotFoundException("End city not found: " + requestDto.getEndCity());
        } catch (FeignException fe) {
            throw new RuntimeException("Route service unavailable when validating endCity", fe);
        }

        // 1. 新建 Trip 实体
        Trip trip = new Trip();
        trip.setName(requestDto.getName());
        trip.setStartDate(requestDto.getStartDate());
        trip.setEndDate(requestDto.getEndDate());

        // 2. 遍历前端传来的 days 列表，生成对应的 TripActivity & TripAccommodation
        if (requestDto.getDays() != null) {
            for (DayDto dayDto : requestDto.getDays()) {
                Integer dayNumber = dayDto.getDay();

                // (A) 如果有活动列表，就为每个 ActivityDto 生成一个 TripActivity
                if (dayDto.getActivities() != null) {
                    for (ActivityDto activityDto : dayDto.getActivities()) {
                        // 创建复合主键
                        TripActivityKey activityKey = new TripActivityKey();
                        // tripId 会在保存时由 JPA 自动填充
                        activityKey.setTripId(null);
                        activityKey.setDay(dayNumber);
                        activityKey.setSequence(activityDto.getSequence());

                        // 构造 TripActivity
                        TripActivity tripActivity = TripActivity.builder()
                                .id(activityKey)
                                .activityId(activityDto.getActivityId())
                                .build();
                        tripActivity.setTrip(trip);
                        trip.getActivities().add(tripActivity);
                    }
                }

                // (B) 如果有住宿（accommodationId）就生成一个 TripAccommodation
                if (dayDto.getAccommodationId() != null) {
                    TripAccommodationKey accommodationKey = new TripAccommodationKey();
                    accommodationKey.setTripId(null);
                    accommodationKey.setDay(dayNumber);

                    TripAccommodation tripAccommodation = TripAccommodation.builder()
                            .id(accommodationKey)
                            .accommodationId(dayDto.getAccommodationId())
                            .build();
                    tripAccommodation.setTrip(trip);
                    trip.getAccommodations().add(tripAccommodation);
                }
            }
        }

        // 3. 保存 Trip（由于 cascade = ALL，会把活动和住宿都一并保存）
        Trip savedTrip = tripDao.save(trip);

        // 4. 将持久化后的 Trip 转成 TripDetail 返回
        return convertToTripDetail(savedTrip);
    }

    /**
     * 更新指定 ID 的 Trip（全部或部分字段可更新）
     */
    @Transactional
    public TripDetail updateTrip(Long tripId, TripRequestDto requestDto) {
        // 1. 从 DB 加载；若不存在，抛 404
        Trip existing = tripDao.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + tripId));

        // 2. 有哪些前端传了，我们就更新哪些字段
        if (requestDto.getName() != null) {
            existing.setName(requestDto.getName());
        }
        if (requestDto.getStartDate() != null) {
            existing.setStartDate(requestDto.getStartDate());
        }
        if (requestDto.getEndDate() != null) {
            existing.setEndDate(requestDto.getEndDate());
        }

        // 3. 如果前端传来了 days（可能要完全替换原来的 activities/accommodations）
        if (requestDto.getDays() != null) {
            // 清除原来所有的活动和住宿
            existing.getActivities().clear();
            existing.getAccommodations().clear();

            // 重新按同样逻辑，遍历 requestDto.getDays() 来填充
            for (DayDto dayDto : requestDto.getDays()) {
                Integer dayNumber = dayDto.getDay();

                // (A) 活动
                if (dayDto.getActivities() != null) {
                    for (ActivityDto activityDto : dayDto.getActivities()) {
                        TripActivityKey activityKey = new TripActivityKey();
                        activityKey.setTripId(tripId);
                        activityKey.setDay(dayNumber);
                        activityKey.setSequence(activityDto.getSequence());

                        TripActivity tripActivity = TripActivity.builder()
                                .id(activityKey)
                                .activityId(activityDto.getActivityId())
                                .build();
                        tripActivity.setTrip(existing);
                        existing.getActivities().add(tripActivity);
                    }
                }

                // (B) 住宿
                if (dayDto.getAccommodationId() != null) {
                    TripAccommodationKey accommodationKey = new TripAccommodationKey();
                    accommodationKey.setTripId(tripId);
                    accommodationKey.setDay(dayNumber);

                    TripAccommodation tripAccommodation = TripAccommodation.builder()
                            .id(accommodationKey)
                            .accommodationId(dayDto.getAccommodationId())
                            .build();
                    tripAccommodation.setTrip(existing);
                    existing.getAccommodations().add(tripAccommodation);
                }
            }
        }

        // 4. 保存并返回更新后的 TripDetail
        Trip updated = tripDao.save(existing);
        return convertToTripDetail(updated);
    }

    /**
     * 删除指定 ID 的 Trip
     */
    @Transactional
    public void deleteTrip(Long tripId) {
        if (!tripDao.existsById(tripId)) {
            throw new ResourceNotFoundException("Trip not found with id " + tripId);
        }
        tripDao.deleteById(tripId);
    }

    /**
     * 查询单个 Trip，返回 TripDetail
     */
    @Transactional(readOnly = true)
    public TripDetail getTrip(Long tripId) {
        Trip trip = tripDao.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + tripId));
        return convertToTripDetail(trip);
    }

    /**
     * 搜索 Trip 列表，支持按行程天数过滤（minDays, maxDays）。
     * startCity / endCity 作为占位，若要真正生效需要在实体里加字段或关联其他表。
     */
    @Transactional(readOnly = true)
    public List<TripSummary> searchTrips(String startCity, String endCity, Integer minDays, Integer maxDays) {
        List<Trip> all = tripDao.findAll();
        return all.stream()
                .filter(trip -> {
                    long days = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
                    if (minDays != null && days < minDays) return false;
                    if (maxDays != null && days > maxDays) return false;
                    // 如果要用 startCity/endCity，此处应补充逻辑
                    return true;
                })
                .map(this::convertToTripSummary)
                .collect(Collectors.toList());
    }

    /**
     * 列出一个 Trip 中所有去过的 activities 与 accommodations（去重）。
     */
    @Transactional(readOnly = true)
    public TripPointsOfInterestDto getPointsOfInterest(Long tripId) {
        Trip trip = tripDao.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + tripId));

        Set<String> activityIds = trip.getActivities().stream()
                .map(TripActivity::getActivityId)
                .collect(Collectors.toSet());

        Set<String> accommodationIds = trip.getAccommodations().stream()
                .map(TripAccommodation::getAccommodationId)
                .collect(Collectors.toSet());

        return new TripPointsOfInterestDto(activityIds, accommodationIds);
    }

    /**
     * 计算某个 Trip 的总天数 = (endDate - startDate) + 1
     */
    @Transactional(readOnly = true)
    public int getDaysCount(Long tripId) {
        Trip trip = tripDao.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id " + tripId));
        return (int) (ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1);
    }

    /**************************************************************************
     * 以下为内部私有方法：将实体 ↔ DTO 相互转换，方便 Controller 层直接返回 DTO
     **************************************************************************/

    /**
     * 将 Trip 实体及其子对象，转换成前端需要的完整结构 TripDetail
     */
    private TripDetail convertToTripDetail(Trip trip) {
        TripDetail detail = new TripDetail();
        detail.setId(trip.getId());
        detail.setName(trip.getName());
        detail.setStartDate(trip.getStartDate());
        detail.setEndDate(trip.getEndDate());

        // 先把日期范围里每一天都初始化一个 DayDto（哪怕某日无活动也要展示空列表或默认空住宿）
        int totalDays = (int) (ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1);
        Map<Integer, DayDto> dayMap = new TreeMap<>();
        for (int i = 1; i <= totalDays; i++) {
            DayDto d = new DayDto();
            d.setDay(i);
            d.setActivities(new ArrayList<>());
            // 默认 accommodationId 保持 null，如果该天有住宿会在下面填充
            dayMap.put(i, d);
        }

        // (A) 把所有 TripActivity 按 day 分组，转换成 ActivityDto
        for (TripActivity ta : trip.getActivities()) {
            Integer dayNum = ta.getId().getDay();
            ActivityDto actDto = new ActivityDto();
            actDto.setSequence(ta.getId().getSequence());
            actDto.setActivityId(ta.getActivityId());

            DayDto targetDay = dayMap.get(dayNum);
            if (targetDay != null) {
                targetDay.getActivities().add(actDto);
            }
        }
        // 每一天的活动，再按 sequence 排序
        for (DayDto d : dayMap.values()) {
            d.getActivities().sort(Comparator.comparing(ActivityDto::getSequence));
        }

        // (B) 把所有 TripAccommodation 填充到对应的 DayDto.accommodationId
        for (TripAccommodation tac : trip.getAccommodations()) {
            Integer dayNum = tac.getId().getDay();
            DayDto targetDay = dayMap.get(dayNum);
            if (targetDay != null) {
                targetDay.setAccommodationId(tac.getAccommodationId());
            }
        }

        detail.setDays(new ArrayList<>(dayMap.values()));
        return detail;
    }

    /**
     * 将 Trip 实体转换成搜索结果摘要 TripSummary
     */
    private TripSummary convertToTripSummary(Trip trip) {
        TripSummary summary = new TripSummary();
        summary.setId(trip.getId());
        summary.setName(trip.getName());
        summary.setStartDate(trip.getStartDate());
        summary.setEndDate(trip.getEndDate());

        int days = (int) (ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1);
        summary.setDaysCount(days);
        return summary;
    }


    /*private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Trip> findAll() {
        return tripDao.findAll();
    }

    @Transactional(readOnly = true)
    public Trip findByIdWithDetails(Long id) {
        return tripDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));
    }

    @Transactional
    public Trip createTrip(TripRequestDto request) {
        Trip trip = new Trip();
        trip.setName(request.getName());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());

        for (DayDto day : request.getDays()) {

            //accommodation
            TripAccommodationKey tripAccommodationKey = new TripAccommodationKey();
            tripAccommodationKey.setDay(day.getDay());

            TripAccommodation tripAccommodation = new TripAccommodation();
            tripAccommodation.setTrip(trip);
            tripAccommodation.setId(tripAccommodationKey);
            tripAccommodation.setTrip(trip);
            tripAccommodation.setAccommodationId(day.getAccommodationId());
            trip.getAccommodations().add(tripAccommodation);

            //activities
            for (ActivityDto a : day.getActivities()) {
                TripActivityKey tripActivityKey = new TripActivityKey();
                tripActivityKey.setDay(day.getDay());
                tripActivityKey.setSequence(a.getOrder());

                TripActivity tripActivity = new TripActivity();
                tripActivity.setId(tripActivityKey);
                tripActivity.setTrip(trip);
                tripActivity.setActivityId(a.getActivityId());
                trip.getActivities().add(tripActivity);
            }
        }
        return tripDao.save(trip);
    }

    @Transactional
    public Trip updateTrip(Long tripId, TripRequestDto request) {
        Trip t = findByIdWithDetails(tripId);
        t.setName(request.getName());
        t.setStartDate(request.getStartDate());
        t.setEndDate(request.getEndDate());

        t.getActivities().clear();
        t.getAccommodations().clear();

        entityManager.flush();

        for (DayDto day : request.getDays()) {
            TripAccommodation acc = new TripAccommodation();
            TripAccommodationKey accKey = new TripAccommodationKey();
            accKey.setDay(day.getDay());
            acc.setId(accKey);
            acc.setTrip(t);
            acc.setAccommodationId(day.getAccommodationId());
            t.getAccommodations().add(acc);

            for (ActivityDto a : day.getActivities()) {
                TripActivity act = new TripActivity();
                TripActivityKey actKey = new TripActivityKey();
                actKey.setDay(day.getDay());
                actKey.setSequence(a.getOrder());
                act.setId(actKey);
                act.setTrip(t);
                act.setActivityId(a.getActivityId());
                t.getActivities().add(act);
            }
        }
        return tripDao.save(t);
    }

    @Transactional
    public void deleteTrip(Long id) {
        tripDao.deleteById(id);
    }

    public List<Trip> searchTrips(LocalDate startDate, LocalDate endDate, Integer minDays, Integer maxDays) {
        return tripDao.findByDateRange(startDate, endDate).stream()
                .filter(t -> {
                    long days = ChronoUnit.DAYS.between(t.getStartDate(), t.getEndDate()) + 1;
                    if (minDays != null && days < minDays) return false;
                    if (maxDays != null && days > maxDays) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }*/
}
