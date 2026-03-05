# RabbitMQ High Availability Cluster

This project demonstrates a **RabbitMQ high-availability cluster** consisting of three RabbitMQ nodes and two Java applications: a **producer** and a **consumer**.  
The applications communicate through a **quorum queue**, which ensures message durability and availability across the cluster.

The goal of this project is to show that:

- Messages continue to be processed even if **one RabbitMQ node fails**.
- The cluster maintains availability thanks to **quorum queue replication**.
- When a stopped node is restarted, it can **rejoin the cluster automatically**.

This setup is useful for learning how RabbitMQ clustering and quorum queues work in practice.

---

# Prerequisites

Make sure the following tools are installed on your system:

- **Docker**
- **Java 17**
- **Maven**

---

# Project Architecture

```
Producer (Spring Boot)
│
▼
RabbitMQ Exchange
│
▼
Quorum Queue
│
▼
Consumer (Spring Boot)

```

RabbitMQ runs as a **3-node cluster**:

```

rabbit1 (cluster seed)
rabbit2
rabbit3

````

The queue uses the **quorum queue type**, which replicates messages using the **Raft consensus algorithm** across the nodes.

---

# Setup

## 1. Start RabbitMQ nodes

From the project root directory, start the containers:

```bash
docker-compose up -d
````

This command will create three RabbitMQ containers:

* `rabbit1`
* `rabbit2`
* `rabbit3`

---

## 2. Join nodes into a cluster

By default, each container starts as an independent RabbitMQ node.
We need to join `rabbit2` and `rabbit3` to the cluster using `rabbit1` as the seed node.

### Connect to the rabbit2 container

```bash
docker exec -it rabbit2 bash
```

Run the following commands inside the container:

```bash
rabbitmqctl stop_app
rabbitmqctl join_cluster rabbit@rabbit1
rabbitmqctl start_app
```

Exit the container and repeat the same steps for `rabbit3`.

After this step, all three nodes should form a single RabbitMQ cluster.

You can verify the cluster status with:

```bash
rabbitmqctl cluster_status
```

---

# Running the Applications

## 1. Build the project

From the project root directory run:

```bash
mvn clean install
```

---

## 2. Start the Consumer

Navigate to the consumer module:

```bash
cd consumer
```

Run the application:

```bash
mvn spring-boot:run
```

During startup the application will automatically create:

* the **exchange**
* the **quorum queue**
* the **binding** between them

---

## 3. Start the Producer

In a new terminal navigate to the producer module:

```bash
cd producer
```

Run:

```bash
mvn spring-boot:run
```

The producer will start sending messages to RabbitMQ.

---

# Verifying the System

Check the **consumer logs**.
You should see messages being consumed that were produced by the producer application.

To test **high availability**, you can stop one of the RabbitMQ nodes:

```bash
docker stop rabbit2
```

The system should continue to process messages normally because the quorum queue still has a majority of nodes available.

After restarting the container:

```bash
docker start rabbit2
```

the node should rejoin the cluster automatically.

---

# Key Concepts Demonstrated

* RabbitMQ **clustering**
* **Quorum queues** for high availability
* **Spring Boot integration** with RabbitMQ
* **Fault tolerance** in distributed messaging systems
* Automatic infrastructure declaration (exchange, queue, binding)

---

# Useful Commands

Check running containers:

```bash
docker ps
```

Check RabbitMQ cluster status:

```bash
docker exec -it rabbit1 rabbitmqctl cluster_status
```

Stop a node to simulate failure:

```bash
docker stop rabbit2
```

Start the node again:

```bash
docker start rabbit2
```

---

# Notes

* Quorum queues require a **majority of nodes** to be available.
  In a 3-node cluster, at least **2 nodes must be running**.
* If two nodes fail simultaneously, the queue will become unavailable until a majority is restored.


