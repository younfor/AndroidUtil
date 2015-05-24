import os
import time
import thread
import time


def run():
    print 'start runing'
    os.system('nohup ./dist_check_and_run.sh &')
thread.start_new_thread(run, ())
# start floop
i = 1
t = 0
while i <= 15:
    time.sleep(20)
    t += 20
    if t % 20 == 0:
        print t, 's go away...'
    if os.path.exists(r'../run_area/server/data.csv'):
        print 'the ', i, ' match'
        if not os.path.exists(r'logdata'):
            print 'create logdata'
            os.mkdir(r'logdata')
        if not os.path.exists(r'logdata/datacsv'):
            print 'create datacsv'
            os.mkdir(r'logdata/datacsv')
        if not os.path.exists(r'logdata/logtxt'):
            print 'create logtxt'
            os.mkdir(r'logdata/logtxt')
        if not os.path.exists(r'logdata/replaytxt'):
            print 'create replaytxt'
            os.mkdir(r'logdata/replaytxt')
        if not os.path.exists(r'logdata/outtxt'):
            print 'create outtxt'
            os.mkdir(r'logdata/outtxt')
        os.system(
            'cp ../run_area/server/data.csv  logdata/datacsv/' + str(i) + '.csv')
        os.system(
            'cp ../run_area/server/log.txt  logdata/logtxt/' + str(i) + '.txt')
        os.system(
            'cp ../run_area/server/replay.txt  logdata/replaytxt/' + str(i) + '.txt')
        os.system(
            'cp ../run_area/works/target/log7777.txt  logdata/outtxt/' + str(i) + '.txt')
        # del
        os.system('rm ../run_area/server/data.csv')
        print 'finish ', i, ' match at ', time.strftime('%Y-%m-%d %H:%M:%S')
        # run
        thread.start_new_thread(run, ())
        i += 1
        t = 0

print 'count the scores:'
os.system('python count.py')
