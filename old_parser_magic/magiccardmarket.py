from Site import *
 
class magiccardmarket(Site):
	def nextPage():
		return ""
		
	def extract(self):
		print self.html.encode('utf-8')
		return ' '
		
		ourTable = []
		#State we keep track of
		inURL = False

		#debut d'une balise image
		tagStart = self.html.find("<img", 0)

		while( tagStart != -1):
		#localise la fin de la balise
			tagEnd = self.html.find('>', tagStart)

			if tagEnd == -1:    #We are done, return the data!
				return ourTable

		#contenu de la balise
			tagText = self.html[tagStart:tagEnd+1]

		#localisation de l'url de l'image
		imgStart=self.html.find('src="', tagStart)
		imgEnd=self.html.find('"', imgStart+5)
		imgURL = self.html[imgStart+5:imgEnd]

		taille=0
		baliseTaille=self.html.find('height="',imgEnd)
		if not baliseTaille == -1:
			baliseTaille=baliseTaille+8
			finBalise= self.html.find(">",imgEnd)
			
			if baliseTaille < finBalise:
				fin=self.html.find('" ',baliseTaille)
				taille=self.html[baliseTaille:fin]

		if (imgURL.find('.jpg')>=0 or imgURL.find('.jpeg')>=0) and (int(taille) > 200 or int(taille)==0):
			ourTable.append(imgURL)
			#print imgURL
			#Look for the NEXT start URL. Anything between the current
			#end tag and the next Start Tag is potential data!
			tagStart = self.html.find("<img", tagEnd+1)

		return(ourTable)
			