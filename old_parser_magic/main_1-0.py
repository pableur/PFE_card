from sgmllib import SGMLParser
from datetime import date, time, datetime
from Site import *
from Card import *
from magiccardmarket import *

import os
import sys

#URLsite = "https://fr.magiccardmarket.eu/Products/Singles/Alpha"

if __name__ == "__main__":
	URLsite=sys.argv[1]
	print "//////////////////////////////////////////////"
	print "///	    putain de cote en ligne	  ///"
	print "//////////////////////////////////////////////"
	print "analyse de "+URLsite
	# creation d'un dossier
	date = datetime.now()
	path='D:/magic/'+str(date.date())
	if not os.path.exists(path):
		os.makedirs(path)
		print "creation de "+path
	os.chdir(path)
	site = Site(URLsite)
	print site.domaine()
	Card("lol").save(path)
	
	
	if site.domaine()=='fr.magiccardmarket':
		site=magiccardmarket(URLsite)
	site.extract()
	#extraction(URLsite)
