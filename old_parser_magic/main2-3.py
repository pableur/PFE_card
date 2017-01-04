from sgmllib import SGMLParser
import urllib2
import os

URLsite = "https://fr.magiccardmarket.eu/Products/Singles/Alpha"
verbose = True

#///////////////////////////////////////////////
#
#	Telechargement de l'image
#
#///////////////////////////////////////////////
def download(url, name):
	name=int(name)
	url = url.decode("utf8")
	print url,"-->",
	try:
		opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
		image = opener.open(url)
	except :
		print "probleme de conexion", url
		return False
	# renome le fichier
	while os.path.isfile(str(name)):
		name=name+1
	# affiche le chemin si le DL a reussi
	try:
		output = open(str(name),'wb')
		output.write(image.read())
		output.close()
	except:
		print "probleme de download", url
		return False
	print os.path.abspath(''),'/',name
	return True

#///////////////////////////////////////////////
#
#	Localisation de l'image dans le code html
#	return la table de la liste des urls de l'image
#
#///////////////////////////////////////////////
def localisationImage(html):
   
    ourTable = []
    #State we keep track of
    inURL = False

    #debut d'une balise image
    tagStart = html.find("<img", 0)

    while( tagStart != -1):
	#localise la fin de la balise
        tagEnd = html.find('>', tagStart)

        if tagEnd == -1:    #We are done, return the data!
            return ourTable

	#contenu de la balise
        tagText = html[tagStart:tagEnd+1]

	#localisation de l'url de l'image
	imgStart=html.find('src="', tagStart)
	imgEnd=html.find('"', imgStart+5)
        imgURL = html[imgStart+5:imgEnd]

	taille=0
	baliseTaille=html.find('height="',imgEnd)
	if not baliseTaille == -1:
		baliseTaille=baliseTaille+8
		finBalise= html.find(">",imgEnd)
		
		if baliseTaille < finBalise:
			fin=html.find('" ',baliseTaille)
			taille=html[baliseTaille:fin]

	if (imgURL.find('.jpg')>=0 or imgURL.find('.jpeg')>=0) and (int(taille) > 200 or int(taille)==0):
		ourTable.append(imgURL)
		#print imgURL
        #Look for the NEXT start URL. Anything between the current
        #end tag and the next Start Tag is potential data!
        tagStart = html.find("<img", tagEnd+1)

    return(ourTable)

class URLLister(SGMLParser):
    def reset(self):
        SGMLParser.reset(self)
        self.urls = []

    def start_a(self, attrs):
        href = [v for k, v in attrs if k=='href']
        if href:
            self.urls.extend(href)

def dejaTraite(name):
	# ajoute l'url au lien visite
	fichier = open("../lienVisite.txt","a+")
	fichier.write(name)
	fichier.write('\n')
	fichier.close()
	# enleve l'url des liens a traiter
	urlATraiter = open("../listeUrlATraiter.txt","r")
	lignesUrlATraiter  = urlATraiter.readlines()
	urlATraiter.close()
	if name[0:4]=='http':
				name=name[7:]
	
	try : 
		if lignesUrlATraiter.remove(name+'\n'):
			print "url supprime"
	except : 
		print "impossible de supprimer l url", name
	del urlATraiter
	urlATraiter = open("../listeUrlATraiter.txt","w")
	for i in range(len(lignesUrlATraiter)):
		urlATraiter.write(lignesUrlATraiter[i])
	urlATraiter.close()
		
def extraction_liens(html, domaine):
	print "lecture des urls"
	parser = URLLister()
	parser.feed(html)

	urlATraiter = open("listeUrlATraiter","a+")
	lienVisite=open("lienVisite.txt","r")	


	lignesUrlATraiter  = urlATraiter.readlines()
	lignesLienVisite  = lienVisite.readlines()

	lienVisite.close()

	# met en forme les lignes
	for i in range(len(lignesUrlATraiter)):
		# convertis les chaines pour la comparaison
		lignesUrlATraiter[i]=lignesUrlATraiter[i].decode('utf8')
		# supprime le www. et le \n
		lignesUrlATraiter[i]=lignesUrlATraiter[i][4:-1]

	for i in range(len(lignesLienVisite)):
		# convertis les chaines pour la comparaison
		lignesLienVisite[i]=lignesLienVisite[i].decode('utf8')
		# supprime le www. et le \n
		lignesUrlATraiter[i]=lignesUrlATraiter[i][4:-1]

# pour toute les URLs de la page
	for url in parser.urls:
		if not url.find(domaine[0],0)>=0 and (url[0:3]=='www' or url[0:4]=='http'):
			if verbose:
				print "not in domaine : ",url
		else:
			present=False
			visite=False
			#supprime http 
			if url[0:3]=='www':
				None
			elif url[0:4]=='http':
				url=url[7:]
			elif not url[0:3]=='www' :
				url='www.'+domaine[0]+domaine[1] + '/' +str(url)
			else:
				None
			
			url=url.decode('utf8')
			# controle si le lien n'as pas deja ete visite
			for i in range(len(lignesLienVisite)):
				if lignesLienVisite[i]==url:
					visite=True
					if verbose:
						print "url deja visite : ",url
					break
			if not visite :
				for i in range(len(lignesUrlATraiter)):
					if lignesUrlATraiter[i] == url:
						present=True
						if verbose:
							print "url deja enregistre : ",url
						break

			if present==False and visite==False:
											
				print 'enregistrement : ', url
				urlATraiter.write(url)
				urlATraiter.write('\n')
				urlATraiter.close()
				urlATraiter = open("../listeUrlATraiter.txt","a+")
				del lignesUrlATraiter
				lignesUrlATraiter  = urlATraiter.readlines()
				for i in range(len(lignesUrlATraiter)):
					# convertis les chaines pour la comparaison
					lignesUrlATraiter[i]=lignesUrlATraiter[i].decode('utf8')
					# supprime '\n'
					lignesUrlATraiter[i]=lignesUrlATraiter[i][0:-1]
		
	urlATraiter.close()
	parser.close()

def extraction(URL):
	prefixe='https://'
	if URL[0:3]=='www':
		URL='https://'+URL
	elif URL[0:len(prefixe)]==prefixe:
		None
	else:
		URL=prefixe+URL
	print "connexion au website ",URL,' ...'
	usock = urllib2.urlopen(URL)

#localisation du domaine
	domaineStart=URL.find(prefixe)
	extension=['.com','.html','.htm','.php','.eu']
	domaineEnd=len(URL)
	extensionDomaine=extension[0]
	for i in extension:
		temp=URL.find(i)
		print temp
		if temp<domaineEnd and temp>=0:
			domaineEnd=temp
			extensionDomaine=i	

        domaine = URL[domaineStart+len(prefixe):domaineEnd]
	print "domaine :",domaine,extensionDomaine

# chargement de la page
	print "lecture de la page"
	html_bytes = usock.read()
	html = html_bytes.decode("utf8")
	usock.close()

# localisation est enregistrement des liens
	extraction_liens(html, [domaine, extensionDomaine])

# Localisation des images
#	print "localisation des images"
#	dataTable = localisationImage(html)
#	if verbose:
#		print "liste d'image"
#		for i in range(len(dataTable)):
#			print "URL :", dataTable[i].encode("utf8")

# Telechargement des images
#	print "telechargement"
#	for i in range(len(dataTable)):		
#		imageUrl=dataTable[i]	
#		download(imageUrl, i)

# Ajoute l'url a url traite
	dejaTraite(URL)
	urlATraiter = open("../listeUrlATraiter.txt","r")
	lignesUrlATraiter  = urlATraiter.readlines()
	urlATraiter.close()

# si la liste des urls a traiter n'est pas vite recommence avec une autre url
	if not lignesUrlATraiter[0]==None:
		print '\n\n'
		extraction(lignesUrlATraiter[0][0:-1])		
	

if __name__ == "__main__":
	print "//////////////////////////////////////////////"
	print "///	    aspirateur a DATA	 	  ///"
	print "//////////////////////////////////////////////"
	# creation d'un dossier "image"
	#if not os.path.isdir("image"):
	#	os.mkdir("image")
	#os.chdir("image")

	extraction(URLsite)
