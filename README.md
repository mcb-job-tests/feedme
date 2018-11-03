Project Instructions : https://github.com/skybet/feedme-tech-test/

- Technology choices

I Developed on a Ubuntu 16.0.04 machine and used the following;

IntelliJ IDEA as the IDE.

Maven to manage a project's build process.

MongoDB (version 4.0.0) as the NoSql datatbase. 
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

Mongo Java Driver 3.8.0
http://mongodb.github.io/mongo-java-driver/3.8/

ZeroMQ
http://zeromq.org/intro:read-the-manual

Junit 4

Other dependencies can be view in project pom.xml

- Instructions

Application creates database named 'feedme' & collection named 'fixtures'

Before running a Test or the main Application, Start MongoDB from a linux terminal:
sudo service mongod start

Start the feeme Service, from the project root directory:
docker-compose up

After running a Test or the main Application, you can view the documents persisted in the fixtures collection of the 'feedme' database:
use feedme
db.fixtures.find().pretty()

- Further Comments

ZeroMq was used to implement the messgae queues for the advanced json packet sharding / partitioning.
In the main Application, an executor service is used to 'simulate' the effect of runnning a single json publisher app and multiple independant NoSQL writers (json consumers) apps.
