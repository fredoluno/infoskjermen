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

må også ha java app-engine installert på google cloud
gcloud components install app-engine-java

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
```
 * * * * * /home/pi/getImageSmallCron.sh &
*/3 * * * * /home/pi/checkwifi.sh
```
## software for selve einkskjemen
https://www.waveshare.com/wiki/6inch_e-Paper_HAT

Mulighet for å få satt opp Wifi
https://github.com/jasbur/RaspiWiFi

endre wifi
sudo nano /etc/wpa_supplicant/wpa_supplicant.conf

mulig kode for å kille alle prosesser før man kjører en ny
for pid in $(ps -ef | grep "some search" | awk '{print $2}'); do kill -9 $pid; done
 for pid in $(ps -ef | grep "IT8951" | awk '{print $2}'); do sudo kill -9 $pid; done




