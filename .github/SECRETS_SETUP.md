# GitHub Actions Secrets Setup Guide

## Required Secrets

Add these secrets to your GitHub repository:

### Docker Hub Credentials
```
DOCKER_USERNAME = wssoftwares
DOCKER_PASSWORD = REDACTED
DOCKER_EMAIL = worldspringsoftwares@gmail.com
```

**How to create Docker Hub token:**
1. Go to https://hub.docker.com/settings/personal-access-tokens
2. Click "Generate New Token"
3. Give it a descriptive name (e.g., "GitHub Actions")
4. Select "Read, Write" permissions
5. Copy the token and use as `DOCKER_PASSWORD`

### Optional: SonarQube Integration
```
SONAR_HOST_URL = https://your-sonarqube-instance.com
SONAR_TOKEN = your-sonarqube-token
```

### Optional: Kubernetes Deployment
```
KUBE_CONFIG = base64-encoded kubeconfig file
```

**How to encode kubeconfig:**
```bash
# On Linux/Mac
cat ~/.kube/config | base64 -w 0

# On Windows PowerShell
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content -Raw ~/.kube/config)))
```

### Database Credentials
```
POSTGRES_USER = baby
POSTGRES_PASSWORD = $inbad1
```

## How to Add Secrets to GitHub

1. Go to your repository on GitHub
2. Click **Settings**
3. Click **Secrets and variables → Actions**
4. Click **New repository secret**
5. Enter name (e.g., `DOCKER_USERNAME`) and value
6. Click **Add secret**

Repeat for all required secrets.

## Verifying Secrets

After adding secrets, verify they're recognized by GitHub:
- Go to **Actions** tab
- Run the CI/CD workflow
- Check the logs to ensure secrets are properly injected

## Security Best Practices

1. **Never commit secrets** - Add `.env` files to `.gitignore`
2. **Rotate tokens regularly** - Update Docker Hub tokens every 90 days
3. **Use specific permissions** - Grant minimum required permissions
4. **Use environment-specific secrets** - Create different secrets for dev/staging/prod
5. **Monitor secret usage** - Review GitHub audit logs
6. **Revoke compromised tokens** - Delete and recreate immediately

## Environment-Specific Secrets (Optional)

For better security, create separate GitHub Environments:

1. Go to **Settings → Environments**
2. Create `development`, `staging`, and `production` environments
3. Add environment-specific secrets:

```
# development environment
DOCKER_USERNAME = your-docker-hub-username
DOCKER_PASSWORD = your-docker-hub-token

# staging environment
DOCKER_USERNAME = your-docker-hub-username
DOCKER_PASSWORD = your-docker-hub-token
KUBE_CONFIG = base64-encoded staging kubeconfig

# production environment
DOCKER_USERNAME = your-docker-hub-username
DOCKER_PASSWORD = your-docker-hub-token
KUBE_CONFIG = base64-encoded production kubeconfig
```

## Testing Secrets in Workflow

Add a debug step to verify secrets (remove before production):

```yaml
- name: Test Secrets
  run: |
    echo "Docker username length: ${#DOCKER_USERNAME}"
    echo "Docker token exists: $([ -n '${{ secrets.DOCKER_PASSWORD }}' ] && echo 'yes' || echo 'no')"
```

## Troubleshooting

### Secret not found in workflow
- Ensure the secret name matches exactly (case-sensitive)
- Verify you're accessing it with `${{ secrets.SECRET_NAME }}`

### Docker login fails
- Check token permissions in Docker Hub
- Verify username and token are correct
- Try rotating the token

### Kubernetes deployment fails
- Verify kubeconfig is properly base64-encoded
- Check cluster connectivity from GitHub runner
- Ensure service account has necessary permissions

## Cleanup

To remove a secret:
1. Go to **Settings → Secrets and variables → Actions**
2. Click the secret you want to remove
3. Click **Delete**
