# https://github.com/rabbitmq/rabbitmq-tutorials/blob/master/java/Send.java
# https://rabbitmq.docs.pivotal.io/36/rabbit-web-docs/api-guide.html

cd /home/RabbitMQ/rabbitmq-tutorials

java -cp target/rabbitmq-example-SNAPSHOT-jar-with-dependencies.jar example.Send
java -cp target/rabbitmq-example-SNAPSHOT-jar-with-dependencies.jar example.Recv

java -cp target/rabbitmq-example-SNAPSHOT-jar-with-dependencies.jar example.Recv2


## test tinh huong 1 gui vao nhieu consumer nhan
