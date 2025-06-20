# Guide de compilation et de lancement

## 1. Cloner le projet

```bash
git clone https://github.com/<organisation>/<projet>.git
cd tripManagement
```

---

## 2. Méthodes pour lancer l’application

| Méthode                                        | Étapes principales                                                                                                                                                                            |
| ---------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **A · IDE (IntelliJ IDEA, Eclipse, VS Code…)** | 1. Ouvrez le dossier **`tripManagement`** dans l’IDE.<br>2. Laissez Maven télécharger les dépendances (*pom.xml*).<br>3. Démarrez chaque micro-service via la commande **Run** (▶️) de l’IDE. |
| **B · Ligne de commande (JAR autonomes)**      | 1. Dans **`tripManagement`**, ouvrez un terminal par service.<br>2. Exécutez :<br>   `bash<br>java -jar jar/<nom_du_service>.jar<br>`                                                         |
| **C · Si notre Kubernetes avait pu marcher, il y aurait eu cette 3ème façon de lancer l’application via Kubernettes/Minikube,ce qui est normalement le plus simple pour user**      | 1. Vérifiez que Minikube est installé.<br>2. Lancez le cluster puis appliquez les manifests :<br>   `bash<br>minikube start<br>kubectl apply -f k8s/<br>`                                     |

---

## 3. Ordre recommandé de démarrage

1. **`service-registry`** (Eureka)
2. **`info-service`**, **`route-service`**, **`trip-service`**
3. **`api-gateway`**

---

## 4. Tester l’API via Swagger UI

Une fois les services actifs, ouvrez :

```
http://localhost:8765/webjars/swagger-ui/index.html
```

Utilisez l’icône en haut à droite pour passer d’un micro-service à l’autre et tester les endpoints.

![image](https://github.com/user-attachments/assets/0d549346-a5b3-40a9-baa1-2ce6f59a29da)

