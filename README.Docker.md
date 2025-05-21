### Building and running your application

When you're ready, start your application by running:
`docker compose up --build`.

Your application will be available at http://localhost:80.

### Deploying your application to the cloud

First, build your image, e.g.: `docker build -t myapp .`.
If your cloud uses a different CPU architecture than your development
machine (e.g., you are on a Mac M1 and your cloud provider is amd64),
you'll want to build the image for that platform, e.g.:
`docker build --platform=linux/amd64 -t myapp .`.

Then, push it to your registry, e.g. `docker push myregistry.com/myapp`.

Consult Docker's [getting started](https://docs.docker.com/go/get-started-sharing/)
docs for more detail on building and pushing.

```
docker run -e PG_HOST='localhost' -e PG_PORT='5432' -e PG_DB='gebit_dev' -e PG_USERNAME='postgres' -e PG_PASSWORD='postgres' -e JWT_ACCESS_SECRET='dGVzdHhzYWRzZHNkc2Fkc2Rzc2Zkc2FmZHN0ZXN0eHNhZHNkc2RzYWRzZHNzZmRzYWZkcw==' -e JWT_REFRESH_SECRET='dGVzdHhzYWRzZHNkc2Fkc2Rzc2Zkc2FmZHN0ZXN0eHNhZHNkc2RzYWRzZHNzZmRzYWZkcw==' -it $(docker build -q .)
```

# Kill all containers
```
docker stop $(docker ps -a -q)
```

start db locally
```
docker container run -d --rm -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=gebit_dev --name db postgres:alpine
```
```
docker run -d --name s3 localstack/localstack -p 4566:4566 -e SERVICES=s3 -e DEBUG=1 -e DATA_DIR=/tmp/localstack/data -e DEFAULT_REGION=eu-west-1 -e AWS_ACCESS_KEY_ID=testkey -e AWS_SECRET_ACCESS_KEY=testsecret -e GATEWAY_LISTEN=0.0.0.0:4566 -v "/var/run/docker.sock:/var/run/docker.sock" -v "./localstack:/docker-entrypoint-initaws.d"
```

