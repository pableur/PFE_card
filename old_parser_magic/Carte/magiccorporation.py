#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

from sgmllib import SGMLParser
from HTMLParser import HTMLParser
import urllib2
import os
from Site import *

url = "http://www.magiccorporation.com/gathering-cartes-edition.html" #"https://fr.magiccardmarket.eu/Expansions"
def controle(zz):
 
    if zz.count(" ")!=0:
        return zz.index(" ")
    else:
        return len(zz)
 
def find(name,url):
	cdc = name
 
	esp = cdc.count(" ")
	deb = 0
	fin = controle(cdc)
	maListe = []
	 
	for i in range(0, esp + 1):
		maListe.append(cdc[deb:fin])
		cdc=cdc[fin+1:]
		fin = controle(cdc)
	 
	for j in maListe:
		if url.find(j)>0 and len(j)>3 and j!='edition'and j!='decks' and j!='deck' and j!='from'and j!='gathering' and j!='magic':
			#print j
			return True
	return False

def addEditionBDD(url):

	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT   id,name
				FROM     edition
				""")
		rows = cursor.fetchall()
		name=""
		idEdition=0
		for i in rows:
			#print i[0]
			if find(i[1].lower(),url.lower()):
				name=i[1]
				idEdition=i[0]

		if name=="":
			conn.close()
			return False
		
					
 		#print name+" -> "+url
		edition=(idEdition,url)
		cursor.execute("""INSERT INTO MagicCorpoEdition(idEdition, URL) VALUES (%s, %s) """,edition)
		conn.commit()
		return True
	except Exception as e:
		print("Erreur")
		#conn.rollback()
	    	print e
		return False
	
	conn.close()
	

# create a subclass and override the handler methods
class MyHTMLParser(HTMLParser):

	def __init__(self):
		HTMLParser.__init__(self)
		self.read=False
		self.lien=False
		self.single=True
		self.url=[]
		self.divCount=0
		self.footer=False
	
	def handle_starttag(self, tag, attrs):
		#print "Encountered an start tag :", tag
		if self.read==True and tag=='a'and self.footer==False:
			#print attrs
			for i in attrs:
				if i[0]=='href':
					print i[1]
					self.url.append(i[1])
		if tag=='div' and len(attrs)>0:	
			if self.read==False:
				self.divCount=+1			
			if attrs[0][0]=='class':
				if attrs[0][1]=='html_div' :
						self.read=True	
			if attrs[0][0]=='id':
				if attrs[0][1]=='footer' :
						self.footer=True
												


	def handle_endtag(self, tag):
	        #print "Encountered an end tag :", tag
		if tag=='div':
			if self.divCount>0:
				self.divCount=-1
			if self.divCount==0:
			#print "fin"
				self.read=False
			
		

	def handle_data(self, data):
		pass


if __name__ == "__main__":
	print "//////////////////////////////////////////////"
	print "///	    Enregistre les editions 	  ///"
	print "//////////////////////////////////////////////"
	
	site=Site(url)
	parser = MyHTMLParser()
	parser.feed(site.html)
	
	for i in parser.url:
		if addEditionBDD(i)==False :
			print "imposible de d'associer l'url : "+i 

