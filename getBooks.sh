read -e -p "Enter number of days back: " DAYS_BACK
read -e -p "Enter username: " USRNM
read -e -p "Enter password: " PWD
read -e -p "Enter date filter MM-dd-yyyy (Optional): " DT

groovy src/main/groovy/GetBooks.groovy $DAYS_BACK $USRNM $PWD $DT
