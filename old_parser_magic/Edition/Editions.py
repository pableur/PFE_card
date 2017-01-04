#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

from sgmllib import SGMLParser
from HTMLParser import HTMLParser
import urllib2
import os
from Site import *
url = "http://magic.wizards.com/node/46383"#"https://fr.magiccardmarket.eu/Expansions"

def addEditionBDD(name, nombreCard,dateExtension,logo,icon, block):
	print ' '
	print block
	print 'edition "'+name+'" '+nombreCard+' cartes '+dateExtension
	print 'logo '+logo
	print 'icon '+icon
	

	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		edition=(name,logo,icon,nombreCard,dateExtension,block)
		requete="UPDATE edition SET nameVO ='"+name+"' WHERE icon LIKE '"+icon+"';"
		print requete
		cursor.execute(requete)
		#cursor.execute("""INSERT INTO edition(name, logo,icon,nombreCard,dateExtension,pack) VALUES (%s, %s,%s,%s,%s,%s) """,edition)
		conn.commit()

	except Exception as e:
		print("Erreur")
		#conn.rollback()
	    	print e
	
	conn.close()
	

# create a subclass and override the handler methods
class MyHTMLParser(HTMLParser):

	def __init__(self):
		HTMLParser.__init__(self)
		self.attribut={"title","nameSet", "quantity","date-display-single","logo","icon"}
		self.editionDictionnaire={}
		self.triggerDictionnaire={}

		for i in self.attribut:
			self.editionDictionnaire[i]=""
			self.triggerDictionnaire[i]=False
	
	def handle_starttag(self, tag, attrs):
		
		if tag=='img':
			temp =attrs[0]
			if temp[1].find('Logo')>0:
				self.editionDictionnaire['logo']=temp[1]
			if temp[1].find('SYMBOL')>0 or temp[1].find('Symbol')>0 or temp[1].find('Icon')>0 :
				self.editionDictionnaire['icon']=temp[1]
		if tag=='li' and len(attrs)>0:		
			if attrs[0][0]=='class':
				if attrs[0][1]=='title':
						self.triggerDictionnaire['title']=True
			
		if tag=='span' and len(attrs)>0:		
			if attrs[0][0]=='class':
				for i in self.attribut:
					if attrs[0][1]==i:
						self.triggerDictionnaire[i]=True


	def handle_endtag(self, tag):
	        #print "Encountered an end tag :", tag
		pass

	def handle_data(self, data):
		for i in self.attribut:
			if self.triggerDictionnaire[i]==True:
				self.editionDictionnaire[i]=data
				if self.triggerDictionnaire[i]!='logo' and self.triggerDictionnaire[i]!='icon':
					self.triggerDictionnaire[i]=False

				if i=='date-display-single':
					pass
				
					addEditionBDD(
				self.editionDictionnaire["nameSet"].strip(),
 				self.editionDictionnaire["quantity"].strip(),
				self.editionDictionnaire["date-display-single"].strip(),
				self.editionDictionnaire["logo"].strip(),
				self.editionDictionnaire["icon"].strip(),
				self.editionDictionnaire["title"].strip())


if __name__ == "__main__":
	print "//////////////////////////////////////////////"
	print "///	    Enregistre les editions 	  ///"
	print "//////////////////////////////////////////////"
	
	site=Site(url)
	parser = MyHTMLParser()
	parser.feed(site.html)

