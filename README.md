

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



"Jackson" module for reading json file
URL to download: 
    https://central.sonatype.com
    
Download the following 3 .jar files:
    jackson-databind       | jackson-databind-2.22.2.jar
    jackson-core           | jackson-core-2.22.2.jar
    jackson-annotations    | jackson-annotations-2.22.2.jar

NOTE: The versions MUST be the same.

Place the 3 .jar files to the following folder:
	
  Your Project/
  └── WebContent/
      └── WEB-INF/
          └── lib/          ← Under this folder
              ├── jackson-databind-2.22.2.jar
              ├── jackson-core-2.22.2.jar
              └── jackson-annotations-2.22.2.jar
              
  
