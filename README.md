# HU2 Gruppenarbeit
[[_TOC_]]
## Beschreibung
Wir haben als Team ein Issue-Tracker (eine Art Aufgabenverwaltung) mit Springboot und Kotlin entworfen und implementiert.

Mit unserer Implemtierung können Aufgaben:
1. erstellt,
2. bearbeitet
3. gelöscht,
4. kommentiert
5. Benutzer verlinkt werden 

Es ist eine kleine sehr einfach gehaltene Version eines [Gitlab-Issue-Verwaltung](https://docs.gitlab.com/ee/user/project/issue_board.html)/[Trello-Boards](https://trello.com/de), was lediglich minimale Funktionalitäten bereit stellt.
- [aktuelle Feautes](./../../wikis/2.1-Feature)


## Dokumentation
0. [Abhängigkeiten](./../../wikis/1.1-Abhängigkeiten)
1. [Architektur](./../../wikis/2-Architektur)
2. [Feautes](./../../wikis/2.1-Feature)
3. [Models](./../../wikis/2.2-Models)
4. API
   - Rest
     - [auth](./../../wikis/3-API/1-REST/1-auth)
     - [user](./../../wikis/3-API/1-REST/2-user)
     - [issue](./../../wikis/3-API/1-REST/3-issue)
     - [comment](./../../wikis/3-API/1-REST/4-comment)
     - [rest-collection](./../../wikis/3-API/1-REST/99-rest-api-collection)
   - Management
     - [actuator](./../../wikis/3-API/3-Management/actuator)
     - [flyway](./../../wikis/3-API/3-Management/flyway)
   - View
     - [stats](./../../wikis/3-API/3-VIEW/stats)
     - [user](./../../wikis/3-API/3-VIEW/user)
     - [view](./../../wikis/3-API/3-VIEW/view)
5. [Nachrichtenversand](./../../wikis/4-Nachrichtenversand)
6. [Rechtesystem](./../../wikis/5-Rechtesystem)
7. [On Boarding](./../../wikis/6-On-Boarding)
8. [WIKI](./../../wikis/pages)


## Start

### Abhängigkeit
Wir benötigen für den Start ein weiteres Repo, das bestimmte Docker-Container bereitstellt, die wir im Backend nutzen.
- Alle Infos dazu [hier]()

### Docker-Container
Um die Docker-Container zu starten führen wir folgende Befehle aus:

```
git clone https://git.thm.de/webservices-2020/aws-docker-orchestration
cd aws-docker-orchestration
docker-compose build
docker-compose up & 
```

### Backend
Wenn die Docker-Container laufen, navigieren wir wieder zurück ins Backend und starten es.


Wenn *gradle* global installiert ist:
```
$> gradle bootRun
```

Alternativ kann das tool im Projekt benutzt werden:
```
$> ./gradlew bootRun
```

### Test
Für die Test müssen auch die Docker-Container laufen!

#### Unit

Wenn *gradle* global installiert ist:
```
$> gradle test
```

Alternativ kann das tool im Projekt benutzt werden:
```
$> ./gradlew test
```

#### Stress-Test
Für einen Stress-Test haben wir ein seperates Repo angelegt:
- [Gatling : Stress-Test](https://git.thm.de/issuetracker/stress-test-gatling)
