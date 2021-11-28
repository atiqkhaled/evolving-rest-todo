How To Launch Todo List Application :
Following things need to install 
1. Java JDK 11
2. Mysql/MariaDb 10.4.17 (user : root, password :)
3. Maven

Following procedure need to be maintain to build jar of application.
* Open terminal.
* Change the current working directory to the location where you want the cloned directory.
* Type `git clone https://github.com/atiqkhaled/todo.git`
* Run maven command `mvn clean install` at same project directory for build `todo-app` jar.
* This `todo-app` jar located at `target` directory of project.

Final procedure to Launch Todo List application
* To launch application need to write command `java -jar todo-app.jar` at jar location.
* While Start up of application, db and table will be created automatically as `todo`

How to know application launch successfully ?
* if `http://localhost:8080/tasks` request on browser found `status : 200` with response `{"_links":{"self":{"href":"http://localhost:8080/tasks"}}}`

Application Navigation Information :
* Find Postman collection on git clone project directory.
* Create task and response will give task list uri `tasks`
* each json object task in task list provide data and navigation information at same time e.g REST. 
 - `_links` define resource transfer from state to state  e.g delete, mark as done, get task for update etc.
 
`
             {
                "id": 1,
                "description": "test 1",
                "priority": "high",
                "status": "Pending",
                "createdAt": "2021-11-28T19:58:35.000+00:00",
                "updatedAt": "2021-11-28T19:58:35.000+00:00",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/tasks/1"
                    },
                    "tasks": {
                        "href": "http://localhost:8080/tasks"
                    },
                    "done": {
                        "href": "http://localhost:8080/tasks/1/mark-done"
                    },
                    "delete": {
                        "href": "http://localhost:8080/tasks/1"
                    }
                }
            }
`
            
 Note : payload for create task and update task `{"description" : "test 2", "priority" : "low"}` and find postman `priority enum` api.
 
 Thanks a lot for reading this.
`
