import os
import time
import thread
import time
import sys
import csv

sh=['73','75','77','81','7777']
name=['jijin','baoshou','luecai','kexing']

for i in name:
    for j in sh:
        os.system('python run.py 1 '+i+' '+j)
