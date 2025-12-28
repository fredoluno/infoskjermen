# infoskjermen

Maven må være installert
Java også
Google cloud sdk


for å kjøre må du ha miljøvariabl satt. 
Setter i en .env fil
GOOGLE_APPLICATION_CREDENTIALS
på min pc: C:\projects\gae\infoskjermen-d678e179a7f5.json
C:\projects\gae\infoskjermen-3db0a1c6f5ca.json

# # Infoskjermen

A Spring Boot application that displays information on a screen, including calendar events, weather data, and public transport information.

## Prerequisites

- Java 21 (Eclipse Adoptium JDK recommended)
- Maven (included via Maven Wrapper)
- Google Cloud Service Account credentials

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd infoskjermen
   ```

2. **Set up Google Cloud credentials**
   - Download your service account key JSON file from Google Cloud Console
   - Place it in a secure location (e.g., `C:/projects/gae/infoskjermen-dc2c58d96b3b.json`)

3. **Create .env file**
   Create a `.env` file in the root directory with your Google Cloud credentials:
   ```
   GOOGLE_APPLICATION_CREDENTIALS=C:/projects/gae/infoskjermen-dc2c58d96b3b.json
   ```

## Running the Application

### Option 1: Using the PowerShell script (Recommended)
```powershell
./run-with-env.ps1
```

### Option 2: Using the batch file (Windows)
```cmd
run-with-env.cmd
```

### Option 3: Manual setup
Set the environment variable manually and run with Maven:
```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS = "C:/projects/gae/infoskjermen-dc2c58d96b3b.json"
./mvnw spring-boot:run
```

### Option 4: Direct Maven (requires environment variable to be set)
```bash
./mvnw spring-boot:run
```

## Accessing the Application

Once started, the application will be available at:
- Main page: http://localhost:8080
- Calendar endpoint: http://localhost:8080/calendar/fredrik
- Weather endpoint: http://localhost:8080/weather/fredrik
- Kindle endpoint: http://localhost:8080/kindle/fredrik

## Configuration

The application uses Google Firestore for configuration storage. Settings are organized by user profiles and include:

- **Google settings**: Calendar API credentials and calendar configurations
- **Weather settings**: YR.no location settings
- **Transport settings**: Entur public transport API settings
- **Netatmo settings**: Smart home sensor configurations
- **Display settings**: UI and theme configurations

## Development

> **Note for AI Assistants and Developers**: This project follows specific coding standards defined in [CODING-STANDARDS.md](CODING-STANDARDS.md). Please review and follow these standards for all code changes, including package structure, Spring Boot patterns, naming conventions, and Git workflow.

### Building
```bash
./mvnw clean compile
```

### Running tests
```bash
./mvnw test
```

### Creating a JAR
```bash
./mvnw package
```

## Architecture

The application follows a Spring Boot architecture with:

- **Controllers**: REST endpoints in `InfoskjermenApplication`
- **Services**: Business logic in `tjenester` package
- **Data Models**: DTOs in `data` package
- **Configuration**: Firestore settings management in `Settings` class
- **Utilities**: Helper classes in `utils` package

## Dependencies

Key dependencies include:
- Spring Boot 3.4.1
- Google Cloud Firestore
- Google API Client Libraries
- dotenv-java (for environment variable management)
- Various HTTP and JSON processing libraries

## Troubleshooting

### Common Issues

1. **PERMISSION_DENIED errors from Firestore**
   - Ensure your service account has proper IAM roles (Editor, Datastore User, Firebase Admin)
   - Verify the credentials file path is correct
   - Check that the project ID matches your Google Cloud project

2. **Application fails to start**
   - Verify Java 21 is installed and JAVA_HOME is set
   - Ensure the .env file exists and contains the correct credentials path
   - Check that the Google Cloud credentials file exists and is readable

3. **Calendar/Google API errors**
   - Verify your Google Cloud project has the Calendar API enabled
   - Ensure the service account has necessary permissions
   - Check that OAuth credentials are properly configured in Firestore

## Scripts

- **run-with-env.ps1**: PowerShell script that loads .env file and starts the application
- **run-with-env.cmd**: Windows batch file equivalent
- **mvnw/mvnw.cmd**: Maven Wrapper for consistent builds across environments: Vurder å bruke Google Workload Identity Federation (WIF) for sikrere autentisering i stedet for service account-nøkkelfil. Se Google Cloud-dokumentasjon for oppsett.

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






ny skjerm er kemkakorr2.local
bruker og passord samme som alle andre

Denne virker lovende
https://github.com/robweber/omni-epd/blob/main/README.md

Bra instrukser her
https://github.com/jhirner/pi-frame/?tab=readme-ov-file


installere node fant jeg her
https://npm.io/package/justini

NOdescript


https://github.com/gnzzz/IT8951?tab=readme-ov-file
