# GitHub Actions Deployment Setup

This repository includes a GitHub Action workflow that automatically deploys the application to Google App Engine when code is pushed to the main/master branch.

## Setup Instructions

### 1. Create a Google Cloud Service Account

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to **IAM & Admin** > **Service Accounts**
3. Click **Create Service Account**
4. Name: `github-actions-deployer`
5. Grant the following roles:
   - App Engine Admin
   - Cloud Build Service Agent
   - Storage Admin
   - Service Account User

### 2. Generate Service Account Key

1. In the Service Accounts list, click on your newly created service account
2. Go to the **Keys** tab
3. Click **Add Key** > **Create New Key**
4. Choose **JSON** format and download the key file

### 3. Add GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** > **Secrets and variables** > **Actions**
3. Click **New repository secret**
4. Name: `GCP_SA_KEY`
5. Value: Copy and paste the entire contents of the JSON key file you downloaded

### 4. Workflow Triggers

The deployment workflow will trigger automatically when:
- Code is pushed to the `main` or `master` branch
- You can also manually trigger it from the Actions tab

### 5. Alternative: Using Workload Identity (Recommended for Production)

For enhanced security, consider setting up [Workload Identity Federation](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-google-cloud-platform) instead of using service account keys.

## Manual Deployment

You can still deploy manually using:
```bash
./mvnw clean package -DskipTests
./mvnw appengine:deploy -DskipTests
```

## Workflow Features

- ✅ Builds the application with Maven
- ✅ Runs tests (continues even if some fail)
- ✅ Caches Maven dependencies for faster builds
- ✅ Deploys to Google App Engine
- ✅ Can be triggered manually
- ✅ Only deploys from main/master branches

## Monitoring Deployments

- View deployment status in the **Actions** tab of your GitHub repository
- Check application logs: `gcloud app logs tail -s default`
- Access your application: https://infoskjermen.uc.r.appspot.com