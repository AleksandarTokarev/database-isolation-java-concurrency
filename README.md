### Database Script
```
create table if not exists account
(
	iban integer not null
		constraint account_pk
			primary key,
	balance double precision
);

INSERT INTO account(iban, balance) VALUES (1, 10);
INSERT INTO account(iban, balance) VALUES (2, 0);

SELECT * FROM account;

After each run do 
UPDATE account SET balance = 10 WHERE iban = 1;
UPDATE account SET balance = 0 WHERE iban = 2;
```
### Call via CURL

```
Transfer
curl --request POST \
  --url http://localhost:8080/transfer \
  --header 'Content-Type: application/json' \
  --data '{
	"from": 1,
	"to":2,
	"amount": 5
}'

curl --request POST \
  --url http://localhost:8080/transferCountdownLatch \
  --header 'Content-Type: application/json' \
  --data '{
	"from": 1,
	"to":2,
	"amount": 5
}'

curl --request POST \
  --url http://localhost:8080/transferCountdownLatch \
  --header 'Content-Type: application/json' \
  --data '{
	"from": 1,
	"to":2,
	"amount": 5
}'

Phaser
curl --request GET --url http://localhost:8080/phaser

ConcurrentMap
curl --request GET --url http://localhost:8080/concurrentDataStructuresMap 

Concurrent List
curl --request GET --url http://localhost:8080/concurrentDataStructuresList 

GetMapValue
curl --request GET --url 'http://localhost:8080/getMapValue?mapKey=Test6' 

GetMapValueWithBarrier
curl --request GET --url 'http://localhost:8080/getMapValueWithBarrier?mapKey=Test6' 
```

### Local Load Testing Tool
`artillery quick --count 20 --num 50 http://localhost:8080/getMapValue`  
The --count parameter above specifies the total number of virtual users, while --num indicates the number of requests that should be made per user.  Quick supports only GET requests  
We can also use a custom yml file to write our own tests/scenarios  
`artillery run artillery.yml`

### Articles and Explanations

https://medium.com/geekculture/transaction-isolation-levels-f438f861e48a

https://vladmihalcea.com/spring-transaction-best-practices/

TLDR;

1.Read Committed Isolation Level - default in most databases - 
There is a Shared/Read Lock, also there is separate Write Lock (single one)  
When T1 Reads A’s balance, it acquires Shared/Read lock.  
Then T1 Writes/Updates A’s balance, and it gets Write Lock.  
Now when another Transaction tries to read the value held under Write lock, it won't be allowed, and it waits until the Write Lock is release or until T1 completes.  

2.Repeatable Reads Isolation Level -   
Readers can block Writers - means if i read something and i plan on reading it later, noone can write  
As discussed above the Read-Committed Isolation level, T1 gets Read Lock and releases it asap once the read is done, and then T2 can come and acquire Write lock to update it.  
What if the Read lock held by T1 is not released asap, and it also prevents Other Transactions to acquire Write lock?  
Then while T1 is reading(any number of times) no other transaction can update this row, hence preventing the non-repeatable read problem.  

3.Serializable Reads Isolation Level -   
When T1 queries a range or records, it acquires a different type of lock, signifying that it belongs to the range.  
This lock is called Range-lock, (Range S-S is its status) instead of S for Read lock and X for the Write lock.  
So when T1 queries a range, all the rows are range-locked.  
If T2 tries to insert a new row, which might affect this range, then T2 will be blocked until T1 completes and releases the Range lock.  
However, T2 can read the rows, as Range-lock allowed shared reads, but prevents certain Writes.  




LOCKS on Databases:
Read(shared) Lock: If T1 holds a Read Lock on a row, then T2 can still read that row.  
Meaning both T1 and T2 can read(shared lock) on the same row.  
Also since T1 holds Read lock, and “Readers don’t block Writers” T2 can still update that row by acquiring the Write Lock.  
Write(exclusive) Lock: If T1 holds a Write Lock on a row, then T2 can NOT read or write to that row. (Writers Block Readers).  
Meaning if Write Lock is set on a Row, no other T can read/Write on that row.  



Local Postgres Docker  
docker run --name myPostgresDb -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=postgresDB -d postgres


### Guava
https://www.baeldung.com/java-list-split  
https://www.geeksforgeeks.org/java-guava-lists-partition-method-with-examples/

### Takeaways
https://stackoverflow.com/questions/1291836/concurrenthashmap-vs-synchronized-hashmap  
