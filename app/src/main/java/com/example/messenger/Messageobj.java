package com.example.messenger;

class Messageobj {
    String name;
    String uid;
    String dialogueuid;
    String text;
    String subtext;
    long timestapm;
    Messageobj(String in_name, String in_text, long in_timestamp){
        name = in_name;
        text = in_text;
        timestapm =  in_timestamp;
    }
    Messageobj(String in_name, String in_text, String in_uid, long in_timestamp){
        name = in_name;
        text = in_text;
        uid = in_uid;
        timestapm =  in_timestamp;
    }
    Messageobj(String in_name, String in_text, String in_uid,String in_dialogueuid, long in_timestamp){
        name = in_name;
        text = in_text;
        uid = in_uid;
        dialogueuid = in_dialogueuid;
        timestapm =  in_timestamp;
    }
    Messageobj(String in_text, long in_timestamp){
        text = in_text;
        timestapm =  in_timestamp;
    }
    String getName(){
        return name;
    }
    String getSecondstroke(){
        long time = (System.currentTimeMillis()/1000) - timestapm;
        if(text.length() > 20){
            subtext = text.substring(0, 20) + "...";
        }
        else{
            subtext = text;
        }
        if(timestapm == -1){
            return ("<font color=#696969>" + subtext +"</font>");
        }
        else if(time < 60){
            return ("<font color=#696969>" + subtext +"</font> <font color=#C0C0C0>just now</font>");
            //return (text + " " + Resources.getSystem().getString(R.string.just_now));
        }
        else if(time < 3600){

            return ("<font color=#696969>" + subtext +"</font> <font color=#C0C0C0>"+ Long.toString(time / 60) + "m ago" +"</font>");
            //return (text + " " + Long.toString(time / 60) + Resources.getSystem().getString(R.string.some_min_ago));
            }
        else{
            return ("<font color=#696969>" + subtext +"</font> <font color=#C0C0C0>"+ Long.toString(time / 3600) + "h ago" +"</font>");
            //сделать сдесь ввод из ресурса
        }
    }

    String getText(){
        return text;
    }
    String getUid() { return uid; }
    String getDialogueuid() { return dialogueuid; }
    long getTime(){
        return timestapm;
    }

}
