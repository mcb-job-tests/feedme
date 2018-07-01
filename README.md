
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

other dependencies can be view in project pom.xml

application creates database named 'feedme' & collection named 'fixtures'

- Instructions
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

As there was no guidence on the required json format to persist, it was assumed the XML API structure should be followed as closely as possible. As a consequence, the NoSql CRUD operation calls are sightly more complex than they needed to be (in order to handle the additional "header" and "body" nesting). However, in a production environment it is unlikely that preservation of "header" and "body" structure would be needed.
