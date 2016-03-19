#-*- coding:utf-8 -*-
import os
import os.path
import sys
import codecs
import traceback
from collections import OrderedDict
import json
import math
import time
import Tkinter as tk
#import Tkinter.ttk as ttk
from tkFileDialog import askopenfilename
from xlrd import open_workbook

def sheet2json(subfolderpath, bookname, sheet):
    sheetname = sheet.name
    resultlist=OrderedDict()
    outputfile = converValue(sheet.cell(0,0).value,"string")
    row=0
    col=0
    try:
        if outputfile=="":
            print (u"表 %s 首个单元格为空，跳过导出!"%(sheetname))
            return
        row = 1
        att_type = []
        for col in range(sheet.ncols):
            typ = converValue(sheet.cell(row, col).value,"string")
            att_type.append(typ)

        #字段的格式"a/b/c@att"
        row = 2
        attribute_row = []
        for col in range(sheet.ncols):
            head = converValue(sheet.cell(row, col).value,"string")
            if head=="":
                print (u"表 %s 导出错误. 行:%d,列:%d, 字段名'%s' 无效!"%(sheetname,row+1,col+1,head))
                return
            attribute_row.append(head)

        for row in range(4,sheet.nrows):
            rowKey=converValue(sheet.cell(row,0).value,"string")

            if rowKey=="":
                print (u"表 %s 导出错误. 行:%d,列:%d, key 无效!"%(sheetname,row+1,0))
            #print rowKey
            resultlist[rowKey] = OrderedDict()
            for col in range(2,sheet.ncols):
                value = sheet.cell(row,col).value
                typ=att_type[col]
                head = attribute_row[col]
                child = resultlist[rowKey]
                appendChild(child,converValue(value,typ),head.split('/'),0,typ)
        #print resultlist
        
        filelist=outputfile.split(",")
        
        filenum=len(filelist)
        for p in filelist:
        	paths=p.split("/")
        	path_index = p.rfind("/")
        	if path_index==-1:
        		path = subfolderpath+"/gen/"+bookname+"/"
        		jsonToFile(resultlist,path,sheetname+".json")
        	else:
        		path=subfolderpath+"/"+p[:path_index]+"/"
        		jsonToFile(resultlist,path,p[path_index+1:])
    #except TypeError, e:
        #print (u"表 %s 导出错误. 行:%d,列:%d, %s"%(sheetname,row+1,col+1,(e.args[0])))
        #print traceback.format_exc()
    except Exception , e:
        #print e.args[0]
        msg = e.args[0]
        #print (u"表 %s 导出错误. 行:%d,列:%d, %s"%(sheetname,row+1,col+1,e.args[0].encode('ascii','ignore')))         #str(e.args[0],errors='ignore')))
        print u"表 {0} 导出错误. 行:{1},列:{2}, {3}".format(sheetname,row+1,col+1,msg)
        print traceback.format_exc()

def jsonToFile(jsondata,path,filename):
	if os.path.exists(path)==False:
		os.makedirs(path)
	file = codecs.open(path+filename, "w", "utf-8")
	file.write("%s" % json.dumps(jsondata, ensure_ascii=False, indent=4))
	file.close()

def appendChild(root,value,heads,n,typ):
    key_num=len(heads)
    key = heads[n]
    if n==key_num-1:
        sub_key = key.split('$')
        sub_key_num = len(sub_key)
        if(sub_key_num==1):
            if typ != "enum":
                root[key] = value
        elif(sub_key_num==2):
            if(value!=0):
                child = root.get(sub_key[0])
                if(child==None):
                    root[sub_key[0]]=sub_key[1]+":"+str(value)
                else:
                    root[sub_key[0]] = child + ","+sub_key[1]+":"+str(value)
        else:
            return
    elif (n<key_num-1):
        child = root.get(key)
        if(child==None):
            root[key]=OrderedDict()
        appendChild(root[key],value,heads,n+1)
    else:
        #
        return
    #root[heads]=value
    return

def converValue(value,typ):
    if isinstance(value,float)==True:

        if typ=="float":
            result= value
        elif typ=="int":
            result= int(value)
        elif typ=="string":
            result= str(int(value))
        elif typ == "enum":
            result = 0
        else:
            raise TypeError(u"无效的类型字段：{0}".format(typ))
            #raise TypeError(u"无效的类型字段：%s"%(typ))
            return
    else:
        if typ=="string":
            result=  value
        elif (value=="" and typ=="int" or typ=="float"):
            result = 0
        elif typ == "enum":
            result = 0
        else:
            raise TypeError(u"{0} : 不是 'float' or 'int'".format(value))
            return 
    if typ=="string":
        result=result.replace('\\','/')
        result=result.strip(' /')
    return result

def processXls(xls_file):
    if xls_file!="":
        workbook = open_workbook(xls_file)
        fvalue = xls_file.rfind('/')
        if(fvalue != -1):
            folder_path,folder_name = os.path.split(xls_file)
            folder_name = folder_name.split('.')[0]
        else:
            folder_name = xls_file.split('.')[0]
            folder_path = '.'

        for sheet in workbook.sheets():
            
            print u"Converting sheet \"%s\" in excel book \"%s\" to JSON" % (sheet.name,folder_name)
            sheet2json(folder_path, folder_name, sheet)
        print "===========All finish !==========="

def export():
    xls_file = askopenfilename()
    _,xls_file=os.path.split(xls_file)
    processXls(xls_file)
    print xls_file

def exportAll():
    for dirpath,dirnames,filenames in os.walk("./"):
        for filename in filenames:
            ext=os.path.splitext(filename)[1]
            if  ext==".xlsx" or ext ==".xls":
                processXls(filename)

if __name__ == '__main__':
    if sys.version[0]=='2':
        pass

    if(len(sys.argv)>1):
        filename = sys.argv[1]
        #print filename
        ext=os.path.splitext(filename)[1]
        if  ext==".xlsx" or ext ==".xls":
            processXls(filename)
    else:
        m_window = tk.Tk()
        m_window.title("fuck excel")
        m_window.geometry("600x400")
        content=tk.Frame(m_window)
        content.grid(row=0,column=0)

        opMsg=tk.StringVar()
        msg_lbl=tk.Label(content,textvariable=opMsg)
        #msg_entry=tk.Entry(content,textvariable=opMsg)
        opMsg.set("ready!!!!\n%s\n"%time.strftime('%d/%b/%Y:%x'))

        open_btn=tk.Button(content,text=u"导出单个文件",command=export)
        open_btn.grid(row=1,column=1)

        #open_btn=tk.Button(content,text=u"export All",command=exportAll)
        #open_btn.grid(row=1,column=3)

        m_window.mainloop()
