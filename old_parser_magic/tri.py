#! /usr/bin/python3.2
# -*- coding: utf-8 -*-

# import des bibliotheques
from skimage import io
from skimage.transform import resize
import os
import shutil

name=0

if __name__ == "__main__":
	if os.path.isdir("image"):
		os.chdir("image")
	# creation d'un dossier "grande-image"
	if not os.path.isdir("grande-image"):
		os.mkdir("grande-image")
	
	while os.path.isfile(str(name)):
		error=0
		fichier=str(name)
		print name," ",
		try:
			im = io.imread(fichier);
		except:
			print "n'existe pas"
			error=1
		if error==0:
			if len(im)<300 or len(im[0])<300:
				print "image trop petite"
			else:
				print "ok"
				shutil.move(fichier,"grande-image")
		
		name=name+1
