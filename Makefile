.PHONY: clean kube-build-image minikube-config minikube-run-db help

PROFILE ?= todoapp
DB_NAME ?= rest-crud
NS ?= ${PROFILE}

.DEFAULT_GOAL := help


build-native:
	env -i PATH=${PATH} ./mvnw package -DskipTests=true -Pnative -Dnative-image.docker-build=true


clean:
	./mvnw clean


kube-build:
	docker build -f src/main/docker/Dockerfile.kubernetes -t todoapp/todoapp .


kube-clean:
	kubectl delete service todoapp
	kubectl delete deployments todoapp
	kubectl delete svc rest-crud
	kubectl delete pods rest-crud


kube-namespace:
	kubectl delete namespace ${NS} || echo "Namespace does not exist"
	kubectl create namespace ${NS}


kube-run-app:
	kubectl run todoapp \
		--image=todoapp/todoapp:latest \
		--port=8080 \
		--image-pull-policy=IfNotPresent
	kubectl expose deployment todoapp --type=NodePort
	minikube service todoapp --url -n todoapp


kube-run-db:
	kubectl run ${DB_NAME} \
		--generator=run-pod/v1 \
		--image=postgres:10.5 \
		--port=5432 \
		--env=POSTGRES_USER=restcrud \
		--env=POSTGRES_PASSWORD=restcrud \
		--env=POSTGRES_DB=rest-crud \
		--image-pull-policy=IfNotPresent
	kubectl expose pod ${DB_NAME}


minikube-config:
	minikube profile ${PROFILE}
	minikube config set memory 4096
	minikube config set cpus 4
	minikube config set disk-size 10GB


minikube-start:
	minikube start
	kubectl create namespace ${NS}
	kubectl config set-context --current --namespace=${NS}


run-db:
	docker run \
		--ulimit memlock=-1:-1 \
		-it \
		--rm=true \
		--memory-swappiness=0 \
		--name db \
		-e POSTGRES_USER=restcrud \
		-e POSTGRES_PASSWORD=restcrud \
		-e POSTGRES_DB=rest-crud \
		-p 5432:5432 postgres:10.5


help:
	echo "TODO"
