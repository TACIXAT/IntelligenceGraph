#scp target/IntelligenceGraph.war opendao.org:/tmp/
#ssh opendao.org -t "sudo cp /tmp/IntelligenceGraph.war /var/lib/tomcat7/webapps/"
#ssh opendao.org -t "sudo service tomcat7 restart && echo 'Sleeping 20...' && sleep 20 && echo 'Copying...' && sudo cp /tmp/IntelligenceGraph.war /var/lib/tomcat7/webapps/"
sudo cp target/IntelligenceGraph.war /var/lib/tomcat7/webapps/
echo "DEPLOYED"
