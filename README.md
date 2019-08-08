# infoskjermen


for å kjøre må du ha miljøvariabl satt
GOOGLE_APPLICATION_CREDENTIALS
på min pc: C:\dev\gae\infoskjermen-d678e179a7f5.json


Kjøre appen lokalt
mvn -DskipTests spring-boot:run

droppe tester
-DskipTests
prodsett
mvn  appengine:deploy

Taile loggen i cloud
gcloud app logs tail

#Google calendar
Refresh token får du ved å følge denne
https://www.youtube.com/watch?v=hfWe1gPCnzc


