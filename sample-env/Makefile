prepare1:
	docker network create monitoring
	docker volume create grafana-volume
	docker volume create influxdb-volume

prepare2:
	docker run --rm \
  	-e INFLUXDB_DB=telegraf -e INFLUXDB_ADMIN_ENABLED=true \
 	-e INFLUXDB_ADMIN_USER=admin \
  	-e INFLUXDB_ADMIN_PASSWORD=supersecretpassword \
  	-e INFLUXDB_USER=telegraf -e INFLUXDB_USER_PASSWORD=secretpassword \
  	-v influxdb-volume:/var/lib/influxdb \
  	influxdb /init-influxdb.sh

