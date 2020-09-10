FROM openjdk:8-jdk
EXPOSE 8080
RUN export LC_ALL=en_US.UTF-8
RUN export LANG=en_US.UTF-8
RUN export LC_TIME=th_TH.UTF-8
RUN apt-get clean && apt-get -y update && apt-get install -y locales && locale-gen en_US.UTF-8 && locale-gen th_TH.UTF-8
RUN locale-gen en_US.UTF-8
ENV TZ=Asia/Bangkok
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ARG JAR_FILE=target/GSBJobEngine.war
COPY ${JAR_FILE} GSBJobEngine.war
ENTRYPOINT ["java","-jar","-Dserver.port=8080","-Djava.io.tmpdir=/usr/local/GSB/logs","-Dspring.jpa.show-sql=false","-Dspring.jpa.hibernate.ddl-auto=update","-Dspring.boot.admin.client.url=http://172.16.0.145:5061","-Dssh.lead.username=soft","-Dssh.lead.password=Soft2","-Dssh.lead.address=172.16.0.145","-Dssh.lead.port=22","/GSBJobEngine.war"]
