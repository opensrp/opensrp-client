*Please find all the codebooks forms in forms folder 
*please read scope document named as "ScopeDocument" in this directory .

Installation Process:

Note:Application uses third party barcode scanner 

# first install Barcode application in Android Tablet . Named as "BarcodeScanner.apk"

#Install OpenSRP-vaccinator apk from its respective release folder . 

User Name : qatest
Password : Admin123

Using Process :

# There are three register in this application (Woman, Child, Field and Monitor).
#Woman and Child register both has two forms each (Enrollment and Followup).
	#*for every form in these registers . one needs qrcode to scan. this can be done by clicking to qrcode icon on each register.
	#* first application searches if there is any person related to that qrcode 
	       *& if 'Yes' , then it shows that record in the view .
		   *& if 'No', then Enrollment option pop up. 
	#* each entity has its own detail view where vaccination is shown . it can be shown by clicking on image icon for that person.	
	
	#* Detail view can also capture image from camera for the patient by click on entity icon .this image can only be shown  in detail view not in register view.
		
	#* For Each next Vaccine there is an alert/schedule which is generated at server end. Please find this scheduling details in 'schedules.xls' file .Application should be able to view those schedules in in Register entity UI. 	


#Field and Monitor register has only one form  but two different views (Monthly , Daily).
  #*its form gets data like wise (monthly,daily)
  

Data View :

# All these entity data can be view at openmrs . url is http://202.141.249.106:6806/openmrs/
