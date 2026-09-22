

# Dependencies

## Add Maven into the project
### Introduction: 
    * Maven is the package management tool for java.
    * In Eclipse, the directory structure of the traditional/classic Maven project is different than "Dynamic Web Project", which is what we need to use. (because our project is a web server project). Therefore, we need to do some conversions to add Maven to this our project.
    * The following the steps to add Maven into this "Dynamic Web Project" 

### Step 01: Open Package Explorer (if haven't)
[top menu] -> [Window] -> [Show View] > [Package Explorer]


### Step 02: Convert project to Maven project
right-click on project name -> [Configure] -> [Convert to Maven project]

   Group Id     : team2
   Artifact Id  : (your project name)
   Version      : 0.0.1-SNAPSHOT
   Packaging    : war


### Step 02: Copy & paste the content
Our-git-hub-project/pom.xml___example


Note: If you need enable external download:
[top menu] -> [Window] -> [Preferences] -> [XML (Wild Web Developer)] -> [Validation & Resolution] -> Check [Allow resolution of external entities]]


### Step 03: Apply & Save Changes
Right-click on your project -> Maven -> Update Project (Alt+F5) → OK










# How to use json reader

### Step 01: Create myConfig.json
Under this folder: /project-root/src/main/java/myConfig.json

With the following format:
```
{
  "_comment": "Copy this file to myConfig.json and fill in your own values. Never commit myConfig.json.",
  "hostname": "localhost",
  "sql_port": "3306",
  "database_name": "xxx_YOUR_LAST_NAME_xxx",
  "database_user": "root",
  "database_pswd": "xxx_YOUR_PASSWORD_xxx",
  "table_name": "student"
}
```

How to use in your code:
```
import cs157a.util.MyConfig;  // Step 01: import MyConfig

public class MyTest {
    public static void main(String [] args) {
        // Step 02: Use the singleton method get(key) to get the value
        String database_name = MyConfig.get("database_name");
        String database_user = MyConfig.get("database_user");
        String database_pswd = MyConfig.get("database_pswd");
        
        System.out.println("database_name: " + database_name);
        System.out.println("database_user: " + database_user);
        System.out.println("database_pswd: " + database_pswd);
                
    } //END main()
} //END public class MyTest
```


