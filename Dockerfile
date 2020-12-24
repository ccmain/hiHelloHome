FROM tomcat:latest

# Delete existing ROOT folder
RUN ["rm", "-rf", "/usr/local/tomcat/webapps/ROOT"]