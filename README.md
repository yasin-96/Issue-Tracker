# HU1
## Beschreibung
Die erste Hausübung besteht in einem simpel gehaltenen Backend mit einer REST-like
API. 

Sie dient als Einarbeitung in die Entwicklung mit dem Spring Framework.
Um schon eine gute Grundlage für das Projekt zu bieten, soll als Datenmodell das Issue
genommen und eine simple CRUD-Funktionalität implementiert werden:

## Issue Model

|Prop | Name | Type | Beschreibung |
|-----|------|------|--------------|
|Id|id|UUID| Eindeutige ID für ein Issue, (UUID in v4)|
|Title| title | varchar(255)| Titel eines Issues|
|Owner| owner | varchar(255)| Verfasser dieses Issues|

## Run App
Wenn *gradle* global installiert ist:
```
$> gradle bootRun
```

Alternativ kann das tool im Projekt benutzt werden:
```
$> ./gradlew bootRun
```

