IKUTIBNB API DOCUMENCTATION

Authentication & Registration 

====> User create its own  account by continue with google with URL below
URL: http://localhost:8080/oauth2/authorization/google


====> user registered 
Method: POst
URL : http://localhost:8080/user
JSON: {
  "fullname": "fahm",
  "email": "brinab9@gmail.com",
  "password": "fahm..03",
  "profilepicture": "data:image/png;base64,your-base64-string",
  "phonenumber": "+255712345678",
  "role": "CLIENT"
}
RESPONCE: {
    "role": "CLIENT",
    "phonenumber": "+255712345678",
    "profilepicture": "http://localhost:8080/uploads/profile_pictures/183b3f9f-53a4-450d-b7c6-0b9cccf74488.png",
    "id": 35,
    "fullname": "fahm",
    "email": "mkali@gmail.com"
}


====> user login
Method: POST
URL : http://localhost:8080/auth/login
JSON : {
        "email": "fahm@gmail.com",
        "password": "fahm..03"
}



====> Get all users 
Method: GET
URL : http://localhost:8080/user
Headers : {
    "Authorization": "Bearer <token>"
}


====> Get user by id
Method: GET
URL : http://localhost:8080/user/:id
Headers : {
    "Authorization": "Bearer <token>"
}


====> Update user by id
Method: PATCH
URL : http://localhost:8080/user/:id
Headers : {
    "Authorization": "Bearer <token>"
}
Amongo the entity in json can be updated
JSON : {
  "fullname": "fahm",
  "email": "brinab9@gmail.com",
  "password": "fahm..03",
  "profilepicture": "data:image/png;base64,your-base64-string",
  "phonenumber": "+255712345678",
  "role": "CLIENT"
}


====> change password
Method: PATCH
URL : http://localhost:8080/user/passwd/:id
JSON : {
  "currentPassword": "12345678",
  "newPassword": "fahm..03"
}


====> Delete user by id
Method: DELETE
URL : http://localhost:8080/user/:id
Headers : {
    "Authorization": "Bearer <token>"
}



