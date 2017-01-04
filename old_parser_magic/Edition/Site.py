#!/usr/bin/env python
# -*- coding: utf-8 -*-

import urllib2

class Site():
	def __init__(self, URL):
		self.URL=str(URL)
		self.domaineName=self.domaine()
		
		usock = urllib2.urlopen(URL)
		temp = usock.read()
		self.html = temp.decode('ISO-8859-1')
		usock.close()
		
		
		if self.domaineName=='fr.magiccardmarket':
			pass
		
	def extract(self):
		print "rien a extraire"
		
	def domaine(self):
		prefixe=['https://','http://']
		domaineStart=0
		for i in prefixe:
			if self.URL.find(i)>=0:
				domaineStart=self.URL.find(i)+len(i)
			
		#localisation du domaine
		extension=['.com','.html','.htm','.php','.eu']
		domaineEnd=len(self.URL)
		extensionDomaine=extension[0]
		for i in extension:
			temp=self.URL.find(i)
			if temp<domaineEnd and temp>=0:
				domaineEnd=temp
				extensionDomaine=i	

			domaine = self.URL[domaineStart:domaineEnd]
		return domaine

