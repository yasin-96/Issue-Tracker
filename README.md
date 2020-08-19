# HU2 Gruppenarbeit
## Beschreibung
Weiterentwicklung des Issuetrackers aus der HU1

## Issue Model

|Prop | Name | Type | Beschreibung |
|-----|------|------|--------------|
|Id|id|UUID?| Eindeutige ID für ein Issue, (UUID in v4)|
|Title| title | String, varchar(255)| Titel eines Issues|
|Owner| owner | String, varchar(255)| Verfasser dieses Issues|


## Comment Model
|Prop | Name | Type | Beschreibung |
|-----|------|------|--------------|
|Id|id| UUID? | Eindeutige ID für ein Issue, (UUID in v4)|
|Content| content | String, varchar(255)| Inhalt eines Kommentares |
|UserId| userId | String, varchar(255)| Verfasser dieses Kommentares|
|IssueId| issueId | String, varchar(255)| Zugehöriges Issues|
|Creation| creation | String| Zeitstempel, wir automatisch befühlt !|

## User Model
|Prop | Name | Type | Beschreibung |
|-----|------|------|--------------|
|Id|id| UUID? | Eindeutige ID für ein Issue, (UUID in v4)|
|Username| username | String, varchar(255)| Name des Users |
|Pasword| password | String, varchar(255)| Password des Users |
|Role| role | String, varchar(255)| Die zugeteilte Role eines Users|


## Run App
Wenn *gradle* global installiert ist:
```
$> gradle bootRun
```

Alternativ kann das tool im Projekt benutzt werden:
```
$> ./gradlew bootRun
```

