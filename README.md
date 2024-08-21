# backend-service
This backend service is a data storage, that allows us to host a vareity of dataset. This gives us two options to store data: in memory storage and  file based storage

###### Default port: 8080

### Endpoints

##### In memory storage:

Accepts JSON format
* POST /{id}
* GET /{id} 
  * /{id}/$value
  * /api/{id}
  * /api/{id}/$value

Accepts JSON, Plain text, XML formats
* POST /api/{id}


To store hashes from the file content
* GET /api/hash/{id}
* POST /api/hash/{id}

##### File based storage
* POST /api/file/{id}
* GET /api/file/{id}
  * /api/file/{id}/$value
