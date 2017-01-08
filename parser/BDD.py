#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 
from types import *

mysqlHost="192.168.118.40"
mysqlUser="root"
mysqlPassword="magicpswd"
mysqlDatabase="test_sql"

class Bdd():
    
    def __init__(self): # Notre méthode constructeur
        self.cursor=None
        self.conn=None
        try:
            self.conn = mysql.connector.connect(host=mysqlHost,
                                                user=mysqlUser,
                                                password=mysqlPassword,
                                                database=mysqlDatabase,
                                                port=3306)
            self.cursor = self.conn.cursor()
        except Exception as e:
            print("Erreur")
            #conn.rollback()
            print e
            
    
    def addEdition(self,name, block, date='0000-00-00', logo='', icon=''):
        name=name.lower()        
        name=self.doubleApos(name)
        print str(block)+' Edition "'+name+'" '+date
        print 'logo '+logo
        print 'icon '+str(icon)
        
        #cherche dans la Bdd l'edition
        requete="SELECT Nom FROM Edition WHERE Nom='"+name+"';"
        print requete        
        self.cursor.execute(requete)
        result=self.cursor.fetchone()
        
        # si l'edition n'est pas présent dans la BDD l'ajoute
        if type(result) is NoneType :            
            requete="INSERT INTO Edition (Nom, Logo, Image, Parution, IdBloc)"
            requete+="VALUES ('"+name+"','"+logo+"', '"+str(icon)+"', '"+date+"','"+str(block)+"');"
            print requete
            self.cursor.execute(requete)
            self.conn.commit()
        
        requete="SELECT id FROM Edition WHERE Nom='"+name+"';"
        self.cursor.execute(requete)
        idEdition=self.cursor.fetchone()
        return idEdition[0]
         	        
    def addBloc(self,name, date='0000-00-00'):
        
        #cherche dans la Bdd le bloc
        name=name.lower()
        name=self.doubleApos(name)
        requete="SELECT Nom FROM Bloc WHERE Nom='"+name+"';"        
        self.cursor.execute(requete)
        result=self.cursor.fetchone()
        
        # si le bloc n'est pas présent dans la BDD l'ajoute
        if type(result) is NoneType :            
            requete="INSERT INTO Bloc (Nom, Parution)"
            requete+="VALUES ('"+name+"', '"+date+"');"
            print requete
            self.cursor.execute(requete)
            self.conn.commit()
            
        # retourn l'ID du bloc
        requete="SELECT id FROM Bloc WHERE Nom='"+name+"';"
        self.cursor.execute(requete)
        idBloc=self.cursor.fetchone()
        return idBloc[0]
    
                    
    def doubleApos(self, string):
        returnString=""
        for i in string:
            returnString=returnString+i
            if i=="'":
             returnString=returnString+"'"
        return returnString
        
    def date(self, jour, mois, annee):
        return str(annee)+"-"+str(mois)+"-"+str(jour)        
        
    def __del__(self):
        self.conn.close()

if __name__ == "__main__":
    print "//////////////////////////////////////////////"
    print "///	    Test de connexion a la BDD         ///"
    print "//////////////////////////////////////////////"
    bdd=Bdd()

    #print bdd.addBloc("khans of tarkir block",bdd.date(1,9,2014))
    #print bdd.addBloc("Theros Block",bdd.date(1,9,2013))    
    #print bdd.addEdition("Theros",1,bdd.date(1,9,2013),"http://magic.wizards.com/sites/mtg/files/images/featured/EN_THS_SetLogo.png","http://magic.wizards.com/sites/mtg/files/images/featured/THS_SetIcon.png")
    #bdd.doubleApos("Dragon's Maze")