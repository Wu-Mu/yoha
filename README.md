## Progress Report
#### Group Name : Yoha
#### Members Info:
11611313 沈毅超

11611410 郑笑典

11611422 王子彦

11611423 李晨昊

11611427 林森

---

### Questions

1. Choose 3 features to implement for this iteration.
  + List the 3 features that you have chosen. Explain the reason for choosing this feature? 
    + Feature 1: Open a camera.  Reason: This is the basic function that our app need to include since our app is dealing with pictures.
    + Feature 2 : Register.  Reason: As we all known, it is fantastic for the users to store their pictures on the cloud rather than saving them in memory on their phone, so we can use the account of the users to record their pictures. And we also can build up a community for the users to share their pictures and download them. Thus we need to account to be customized. 
    + Feature 3: Login. Reason: As mentioned above.
    + Feature 4： Read the photo from album. This is also a basic function that our app needs to include.
    

  + Write 2 test scenario for each feature. These test cases serves as the specification for each feature.

    - Test scenario 1

      Feature 1: When open the app, user will be asked whether give this app permission to control the camera. If the user choose 'yes', the app can normally be used.

    - Test scenario 2

      Feature 1: When open the app, user will be asked whether give this app permission to control the camera. If the user choose 'no', the app can not be used and exit.

    - Test scenario 3

      Feature 2: We use postman to test register and login feature.

      When register success.

      ![register_test1](/pic/register_test1.png)

    - Test scenario 4

      Feature 2:

      When the username is existed.

      ![register_test2](/pic/register_test2.png)

    - Test scenario 5

      Feature 3:

      When login success.

      ![login_test1](/pic/login_test1.png)

    - Test scenario 6

      Feature 3:

      Login with wrong password.

      ![login_test2](/pic/login_test2.png)

    - Test scenario 7

      Feature 3

      Login with wrong username.

      ![login_test3](/pic/login_test3.png)
    
    - Test scenario 8
      
      Feature 4
      
      When open the album, don't choose any photo, just come back, pass no picture to the app, then find no error for the app--still continue run the page before open the camera.
      
    - Test scenario 9
      
      Feature 4
      
      Passing a big size picture to the app by album, it still works.
      
      
2. Schedule after week 10
  + Week 11: Build up the change style page the beautify all the page of our page, set a basic picture transform module on our app
  + Week 12: Realize the feature: Throw your photo to the trash can and empty the trash can
  + Week 13: Improve the model we used in the transfomation and expand several styles.
  + Week 14: Build up a community on our app and improve the quality of our tansformation models.
  + Week 15: Test and debug.
