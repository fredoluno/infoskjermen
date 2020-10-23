# infoskjermen

Maven må være installert
Java også
Google cloud sdk


for å kjøre må du ha miljøvariabl satt
GOOGLE_APPLICATION_CREDENTIALS
på min pc: C:\projects\gae\infoskjermen-d678e179a7f5.json
C:\projects\gae\infoskjermen-3db0a1c6f5ca.json


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

OK


# Selve skjemen. 
koble på med putty
bruker pi
passord er det i 1password

Samba er installert
samme bruker 


##cron
crontab -e for å editere cron. Sctiptet som kjøres er:

 "* * * * * /home/pi/getImageSmallCron.sh &"

## software for selve einkskjemen
https://www.waveshare.com/wiki/6inch_e-Paper_HAT


