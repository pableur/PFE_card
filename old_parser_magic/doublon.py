#! /usr/bin/python3.2
# -*- coding: utf-8 -*-

# import des bibliotheques
from skimage import io
from skimage.transform import resize
import os
import shutil

name=0

if __name__ == "__main__":
	if os.path.isdir("image/grande-image"):
		os.chdir("image/grande-image")
	# creation d'un dossier "grande-image"
	if not os.path.isdir("doublon"):
		os.mkdir("doublon")
	
	while name<1800:
		error=0
		fichier=str(name)
		print "image ",name
		try:		
			im = io.imread(fichier);
		except:
			error=1
		if error==0:
			nameDoublon=name+1
			while nameDoublon<1800:
				
				error2=0
				if name==nameDoublon:
					error2=2
				else:
					try:
						imDoublon = io.imread(str(nameDoublon));
					except:
						error2=1
				if error2==0:
					#print 'image ',fichier, 'comparaison avec ',nameDoublon,
					if not len(im)==len(imDoublon) or not len(im[0])==len(imDoublon[0]):
						#print "image differente"
						None
					else:
						doublon = True
						for i in range(len(im)/10):
							if doublon == False:
								break
							for j in range(len(im[0])/10):
								if doublon == False:
									break
								# si diffetent
								for k in range(3):
									if not im[i*10][j*10][k] == imDoublon[i*10][j*10][k]:
										doublon = False
										#print "image differente"
										break
						if doublon == True:							
							print 'image ',fichier, 'comparaison avec ',nameDoublon, "doublon"
							shutil.move(str(nameDoublon),"doublon")
					
				nameDoublon=nameDoublon+1		
		name=name+1
