scp target/IntelligenceGraph.war opendao.org:/tmp/
#ssh opendao.org -t "sudo cp /tmp/IntelligenceGraph.war /var/lib/tomcat7/webapps/"
ssh opendao.org -t "sudo service tomcat7 restart && echo 'Sleeping 30...' && sleep 30 && echo 'Copying...' && sudo cp /tmp/IntelligenceGraph.war /var/lib/tomcat7/webapps/"
echo "DEPLOYED"
