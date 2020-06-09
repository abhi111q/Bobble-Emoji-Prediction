package com.example.bobbleemojiprediction;

import java.util.regex.*;
import java.util.*;

public class TextValidation{

    public static String validated_string(String data){
        String text= data;

        text= convert_linebreaks(text);
        text = remove_variation_selectors(text);
        text= remove_control_chars(text);

        Boolean valid_text= false;
        Boolean latin_flag = false;

        Pattern english_check= Pattern.compile("[a-zA-Z0-9]");

        Integer c=0;

        List<String> bad_chars= new ArrayList<String>(Arrays.asList("@","#","$","%","^","&","*","(",")","[","]",";","?","/","{","}","-","_","=","+","\n","'",":","<",">","~","`","|","\\","\""));

        if (text == "\n"){
            return "";
        }

        List<String> remove_char = new ArrayList<String>();

        char[] chars = text.toCharArray();

        for (char d : chars) {
            if(english_check.matcher(Character.toString(d)).matches()){
                latin_flag=true;
            }

            if((english_check.matcher(Character.toString(d)).matches()==true) || (d ==' ') || (Pattern.matches("\\p{Punct}", Character.toString(d))) || (d== '\n') ) {
                c+=1;
                if(bad_chars.contains(Character.toString(d))){
                    remove_char.add(Character.toString(d));
                }
            }
        }

        if(c== text.length() && latin_flag){
            valid_text = true;
        }else{
            return "";
        }
        for (String d : remove_char) {
            text= text.replace(d, " ");
        }

        text = text.replaceAll("\\n+"," ");
        text = text.replaceAll("\\d+", "x");
        text= text.replaceAll("\\.+", ".");
        text = text.replaceAll("\\,+", ",");
        text = text.replaceAll("\\!+", "!");
        text = text.replaceAll("\\?+", "?");
        text = text.replaceAll("'\'+", "'");
        text = text.replace("\"","");
        text = text.trim();
        text = text.replaceAll("\\s+" , " ");

        return text;
    }

    public static String reject_space_punct(String text){
        Boolean latin_found = false;
        Integer count_latin_char = 0;
        Integer punctuation_count =0;
        Integer space_count = 0;

        Pattern english_check = Pattern.compile("[a-z]");

        char[] chars = text.toCharArray();
        for (char c : chars) {
            if(english_check.matcher(Character.toString(c)).matches()){
                count_latin_char+=1;
                latin_found=true;
            }
            if(c==',' || c=='.' || c=='!'){
                punctuation_count+=1;
            }

            if(c==' '){
                space_count+=1;
            }
        }

        Integer text_len= text.length();
        if(count_latin_char <2){
            return "reject";
        }
        else{
            if(space_count > 0.5*text_len){
                return "reject";
            }

            if(punctuation_count > 0.6*text_len){
                return "reject";
            }
            return "accept";
        }

    }

    public static String extrapolate_string(String text){

        if(text.length()< 60){
            String new_text = text;
            for(int i=0;i<60-text.length();i++){
                new_text+='-';
            }
            return new_text;
        }
        else if (text.length()==60) {
            return text;
        } else {
            return text.substring(0,Math.min(text.length(), 60));
        }
    }

    public static List<Integer> ma_in(String text){

        Map<Character,Integer> vocab = new HashMap<Character, Integer>(){{
            put('b', 0);put('e', 1); put('s', 2); put('t', 3);put(' ', 4); put('a', 5);put('k', 6); put('u', 7); put('c', 8); put('h', 9); put('d', 10); put('r', 11); put('m', 12); put('-', 13); put('z', 14); put('i', 15); put('j', 16);
            put('n', 17); put('y', 18); put('p', 19); put('o', 20); put('w', 21); put('l', 22); put('g', 23); put('f', 24); put('"', 25);put('v', 26); put('_', 27); put('x', 28); put('q', 29);
        }};

        String valid_text= validated_string(text);
        String token = reject_space_punct(text);

        if(token=="accept"){
            String text_extrapolated = extrapolate_string(valid_text);

            List<Character> sentence = new ArrayList<Character>();

            char[] chars= text_extrapolated.toCharArray();
            for (char c : chars) {
                sentence.add(c);
            }
            List<Integer> sentence_predict = new ArrayList<Integer>();
            for (char  s: sentence) {
                sentence_predict.add(vocab.get(s));
            }
            return sentence_predict;

        }
        else{
            List<Integer> empty_list = new ArrayList<Integer>();
            System.out.println("Not a valid text");
            return empty_list;
        }
    }

    public static String convert_linebreaks(String text){
        List<String> r= new ArrayList<String>(Arrays.asList("\\\\n", "\\n", "\n", "\\\\r", "\\r", "\r", "<br>"));
        for (String string : r) {
            text= text.replace(string, " ");
        }
        return text;
    }


    public static String remove_control_chars(String text){
        return text;
    }

    public static String remove_variation_selectors(String text){
        List<String> VARIATION_SELECTORS = new ArrayList<String>(Arrays.asList("\ufe00","\ufe01","\ufe02","\ufe03","\ufe04","\ufe05","\ufe06","\ufe07","\ufe08","\ufe09","\ufe0a","\ufe0b","\ufe0c","\ufe0d","\ufe0e","\ufe0f"));
        for (String string : VARIATION_SELECTORS) {
            text= text.replace(string, "");
        }
        return text;
    }


}
