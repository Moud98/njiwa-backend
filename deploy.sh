#!/bin/bash







































































































































































































































































































5. Document runbooks for common operations4. Configure backup strategies for databases3. Set up monitoring and alerting2. Test the CI/CD pipeline with a small change1. Configure GitHub secrets## Next Steps- Enable RBAC for fine-grained access control- Kubernetes network policies can restrict pod communication- GitHub secret scanning prevents accidental commits- SonarQube identifies code quality issues- Trivy scans for container vulnerabilities## Security Considerations5. **Pod crashes**: Check logs: `kubectl logs deployment/springboot-app -n spring-app --previous`4. **Image push fails**: Ensure Docker Hub has enough storage quota3. **Tests fail in pipeline**: Run `./mvnw clean test` locally to debug2. **Kubernetes connection error**: Check KUBE_CONFIG secret is properly base64-encoded1. **Docker login fails**: Verify Docker Hub credentials and token permissionsFor common issues:## Support & Troubleshooting```docker image prune -a# Remove old imagesdocker images | grep ikutibnb# Check docker image locallygit push origin develop --force# Force push to trigger pipeline (use with caution)git show HEAD# View committed changesgit log --oneline -n 5# Check pipeline status```bash## Useful Commands   - Keep rollback scripts ready   - Test migrations in staging first   - Ensure migrations run before app startup5. **Database migrations**   - Use environment-specific secrets   - Never commit secrets to git   - Rotate Docker Hub tokens regularly4. **Keep secrets secure**   - Set up alerts for failed deployments   - Monitor logs in real-time   - Check `/actuator/health` endpoint3. **Monitor deployment health**   - GitHub Actions will automatically tag images   - Create tags like `v1.0.0`, `v1.0.1`, etc.2. **Use semantic versioning for releases**   ```   docker-compose up -d   docker build -t test:local .   ./mvnw clean test   ```bash1. **Always test locally before pushing**## Best Practices```# Access at http://localhost:8081kubectl port-forward svc/springboot-app 8081:8081 -n spring-app# Forward local port to service```bash### Port Forwarding```kubectl rollout undo deployment/springboot-app -n spring-app# Rollback to previous versionkubectl rollout history deployment/springboot-app -n spring-app# View rollout history```bash### Rollback Deployment```kubectl get events -n spring-app# Check eventskubectl describe deployment springboot-app -n spring-app# Describe deploymentkubectl logs deployment/springboot-app -n spring-app --previous# Previous pod logs (if crash loop)kubectl logs deployment/springboot-app -n spring-app -f# Pod logs```bash### View Kubernetes Logs```# In GitHub UI: Actions tab → Select workflow → View logs```bash### View GitHub Actions Logs## Monitoring & Troubleshooting- Can be modified for high availability- Identical to staging setup- Requires confirmation before deployment### Production (Kubernetes)- Health checks enabled- 2 replicas by default- PostgreSQL StatefulSet with persistent volume- Uses Kubernetes cluster### Staging (Kubernetes)- Hot reload enabled- App runs on `localhost:8081`- Uses local PostgreSQL container### Development (docker-compose)## Environment Configuration4. Deployment starts automatically3. Select environment and image tag2. Click **Run workflow**1. Go to **Actions → Deploy to Kubernetes**Use GitHub Actions UI:### Manual Kubernetes Deployment```Code Push → Tests → Build → Push to Docker Hub → Deploy to Production```### Main Branch (`main`)```Code Push → Tests → Build → Push to Docker Hub → Deploy to Dev```### Development Branch (`develop`)## Deployment Workflow```kubectl logs -f deployment/springboot-app -n spring-appkubectl get pods -n spring-app# Check statuskubectl apply -f k8s/app-service.yamlkubectl apply -f k8s/app-deployment.yaml# Deploy applicationkubectl apply -f k8s/postgres-service.yamlkubectl apply -f k8s/postgres-statefulset.yamlkubectl apply -f k8s/postgres-pvc.yaml# Deploy database  --from-literal=POSTGRES_PASSWORD='$inbad1'  --from-literal=POSTGRES_USER=baby \  -n spring-app \kubectl create secret generic db-credentials \kubectl apply -f k8s/namespace.yaml# Create namespace and secrets```bash### Manual Kubernetes Deployment```docker-compose up -d# Run with docker-composedocker push yourusername/ikutibnb-backend:latest# Push the imagedocker login# Login to Docker Hubdocker build -t yourusername/ikutibnb-backend:latest .# Build the image```bash### Manual Docker Deployment```DOCKER_USERNAME=yourusername DOCKER_PASSWORD=yourtoken ./deploy.sh prod# Deploy to production (Kubernetes)DOCKER_USERNAME=yourusername DOCKER_PASSWORD=yourtoken ./deploy.sh staging# Deploy to staging (Kubernetes)./deploy.sh dev# Deploy to development (Docker Compose)chmod +x deploy.sh# Make script executable```bash### Using the Deployment Script## Local Deployment3. Add required secrets per environment2. Create `development`, `staging`, and `production` environments1. Go to **Settings → Environments**For better control, create environments:### 4. Create GitHub Environments (Optional)```# Add the content as KUBE_CONFIG secret on GitHubcat ~/.kube/config | base64 -w 0 > kube_config_base64.txt# Encode your kubeconfig as base64```bash### 3. Prepare kubeconfig for Kubernetes Deployment3. Use this token as `DOCKER_PASSWORD` secret2. Create a new token with read/write permissions1. Go to https://hub.docker.com/settings/personal-access-tokensInstead of using your password, create a Personal Access Token:### 2. Configure Docker Hub Access Token```KUBE_CONFIG             # Base64-encoded kubeconfig filePOSTGRES_PASSWORD       # Database password (default: $inbad1)POSTGRES_USER           # Database username (default: baby)SONAR_TOKEN             # Your SonarQube token (optional)SONAR_HOST_URL          # Your SonarQube server URL (optional)DOCKER_EMAIL             # Your Docker Hub emailDOCKER_PASSWORD          # Your Docker Hub password (use a Personal Access Token)DOCKER_USERNAME          # Your Docker Hub username```Go to **Settings → Secrets and variables → Actions** and add the following secrets:### 1. Add GitHub Secrets## Setup Instructions- Waits for deployment to be ready- Performs health checks- Deploys Spring Boot application- Deploys PostgreSQL StatefulSet- Creates Kubernetes secrets and ConfigMaps**Features**:- `image_tag`: Specify the Docker image tag to deploy- `environment`: Choose between `staging` or `production`**Parameters**:**Triggers**: Manual workflow dispatch### 2. Kubernetes Deployment (`deploy-k8s.yml`)- **code-quality**: Runs SonarQube analysis- **security-scan**: Runs Trivy to scan for vulnerabilities- **deploy-prod**: Deploys to production environment (when pushing to `main`)- **deploy-dev**: Deploys to development environment (when pushing to `develop`)- **build**: Builds the application and pushes Docker image to Docker Hub- **test**: Runs unit tests with PostgreSQL service**Jobs**:**Triggers**: Push to `develop` or `main` branches, Pull Requests### 1. CI/CD Pipeline (`ci-cd.yml`)## GitHub Actions Workflows- **Deployment**: Automated deployment to dev/staging/prod environments- **Security Scanning**: Trivy vulnerability scanning and SonarQube code quality analysis- **Building**: Maven build and Docker image creation- **Testing**: Automated unit tests on every push and pull requestThis project uses GitHub Actions for continuous integration and deployment. The pipeline includes:## Overview# Deployment script for ikutibnb-backend
# Usage: ./deploy.sh [dev|staging|prod]

set -e

ENVIRONMENT=${1:-dev}
DOCKER_USERNAME=${DOCKER_USERNAME:-yourusername}
DOCKER_PASSWORD=${DOCKER_PASSWORD}
IMAGE_NAME="$DOCKER_USERNAME/ikutibnb-backend"
IMAGE_TAG="latest"
NAMESPACE="spring-app"
K8S_FOLDER="./k8s"
MAVEN_CMD="./mvnw"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print messages
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|staging|prod)$ ]]; then
    print_error "Invalid environment: $ENVIRONMENT"
    echo "Usage: ./deploy.sh [dev|staging|prod]"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running"
    exit 1
fi

print_info "Building JAR..."
$MAVEN_CMD -B -DskipTests clean package

print_info "Building Docker image..."
docker build -t "$IMAGE_NAME:$IMAGE_TAG" \
             -t "$IMAGE_NAME:$ENVIRONMENT" \
             -t "$IMAGE_NAME:$(date +%s)" .

print_info "Logging into Docker Hub..."
if [ -z "$DOCKER_PASSWORD" ]; then
    echo "Enter Docker password:"
    read -s DOCKER_PASSWORD
fi
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

print_info "Pushing Docker image to registry..."
docker push "$IMAGE_NAME:$IMAGE_TAG"
docker push "$IMAGE_NAME:$ENVIRONMENT"

case $ENVIRONMENT in
    dev)
        print_info "Deploying to development (Docker Compose)..."
        docker-compose down || true
        docker-compose up -d
        print_info "Waiting for services to be healthy..."
        sleep 10
        print_info "Checking backend health..."
        if curl -f http://localhost:8081/actuator/health 2>/dev/null; then
            print_info "Health check passed!"
        else
            print_warning "Health check may have failed, but continuing..."
        fi
        print_info "Development deployment completed!"
        print_info "Access the application at http://localhost:8081"
        print_info "View logs with: docker-compose logs -f backend"
        ;;
    
    staging)
        print_info "Deploying to staging (Kubernetes)..."
        
        # Create namespace
        kubectl apply -f ${K8S_FOLDER}/namespace.yaml
        
        # Create docker-registry secret
        kubectl delete secret regcred -n ${NAMESPACE} --ignore-not-found=true
        kubectl create secret docker-registry regcred \
          --docker-server=docker.io \
          --docker-username=$DOCKER_USERNAME \
          --docker-password="$DOCKER_PASSWORD" \
          -n ${NAMESPACE}
        
        # Apply DB secret
        kubectl apply -f ${K8S_FOLDER}/secret-db.yaml -n ${NAMESPACE}
        
        # Apply Postgres
        kubectl apply -f ${K8S_FOLDER}/postgres-pvc.yaml -n ${NAMESPACE}
        kubectl apply -f ${K8S_FOLDER}/postgres-service.yaml -n ${NAMESPACE}
        kubectl apply -f ${K8S_FOLDER}/postgres-statefulset.yaml -n ${NAMESPACE}
        
        # Apply ConfigMap
        kubectl apply -f ${K8S_FOLDER}/onfigmap-app.yaml -n ${NAMESPACE} || true
        
        # Apply app with correct image
        tmp_manifest=$(mktemp)
        sed "s|REPLACE_WITH_IMAGE:TAG|$IMAGE_NAME:$ENVIRONMENT|g" ${K8S_FOLDER}/app-deployment.yaml > ${tmp_manifest}
        kubectl apply -f ${tmp_manifest} -n ${NAMESPACE}
        rm -f ${tmp_manifest}
        
        kubectl apply -f ${K8S_FOLDER}/app-service.yaml -n ${NAMESPACE}
        
        print_info "Waiting for deployment to be ready..."
        kubectl rollout status deployment/springboot-app -n ${NAMESPACE} --timeout=5m
        print_info "Staging deployment completed!"
        ;;
    
    prod)
        print_info "Deploying to production (Kubernetes)..."
        read -p "⚠️  Are you sure you want to deploy to PRODUCTION? (yes/no): " confirm
        if [[ "$confirm" != "yes" ]]; then
            print_error "Production deployment cancelled"
            exit 1
        fi
        
        # Create namespace
        kubectl apply -f ${K8S_FOLDER}/namespace.yaml
        
        # Create docker-registry secret
        kubectl delete secret regcred -n ${NAMESPACE} --ignore-not-found=true
        kubectl create secret docker-registry regcred \
          --docker-server=docker.io \
          --docker-username=$DOCKER_USERNAME \
          --docker-password="$DOCKER_PASSWORD" \
          -n ${NAMESPACE}
        
        # Apply DB secret
        kubectl apply -f ${K8S_FOLDER}/secret-db.yaml -n ${NAMESPACE}
        
        # Apply Postgres
        kubectl apply -f ${K8S_FOLDER}/postgres-pvc.yaml -n ${NAMESPACE}
        kubectl apply -f ${K8S_FOLDER}/postgres-service.yaml -n ${NAMESPACE}
        kubectl apply -f ${K8S_FOLDER}/postgres-statefulset.yaml -n ${NAMESPACE}
        
        # Apply ConfigMap
        kubectl apply -f ${K8S_FOLDER}/onfigmap-app.yaml -n ${NAMESPACE} || true
        
        # Apply app with correct image
        tmp_manifest=$(mktemp)
        sed "s|REPLACE_WITH_IMAGE:TAG|$IMAGE_NAME:$ENVIRONMENT|g" ${K8S_FOLDER}/app-deployment.yaml > ${tmp_manifest}
        kubectl apply -f ${tmp_manifest} -n ${NAMESPACE}
        rm -f ${tmp_manifest}
        
        kubectl apply -f ${K8S_FOLDER}/app-service.yaml -n ${NAMESPACE}
        
        print_info "Waiting for deployment to be ready..."
        kubectl rollout status deployment/springboot-app -n ${NAMESPACE} --timeout=5m
        print_info "Production deployment completed!"
        ;;
esac

print_info "✅ Deployment to $ENVIRONMENT completed successfully!"
print_info "Check pods with: kubectl get pods -n $NAMESPACE"
                                                                                                                                                                    