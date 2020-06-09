package com.example.bobbleemojiprediction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Collections;
import java.util.List;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "model_48_lacs_full_model.tflite";
    private Interpreter tflite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText text = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        TextView result= (TextView)findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sentence= text.getText().toString().toLowerCase().trim();
                String second_Sentence = "jai";

                List<Integer> list_by_python_code = TextValidation.ma_in(sentence);
                List<Integer> list_by_python_code_2nd_sentence = TextValidation.ma_in(second_Sentence);
                float[][] darray = new float[2][60];
                for(int i=0;i<2;i++){
                    for(int j=0;j<60;j++){
                        if(i==0)
                            darray[i][j] = list_by_python_code.get(j);
                        else
                            darray[i][j] = list_by_python_code_2nd_sentence.get(j);
                    }
                }

                float[][] predicted_result = classify(darray);
                Log.i("msg", Arrays.toString(predicted_result[0]));
                float[] actual_array_of_input_sentence = new float[predicted_result[0].length];

                for (int i=0;i<predicted_result[0].length;i++){
                    actual_array_of_input_sentence[i]= predicted_result[0][i];
                }
                TreeMap<Float,Integer> result_match = new TreeMap<>(Collections.reverseOrder());

                for(int i=0;i<predicted_result[0].length;i++){
                    result_match.put(predicted_result[0][i],i);
                }
                String emojis = "";
                int count =0;
                for(Map.Entry<Float,Integer> entry : result_match.entrySet()) {
                    Float key = entry.getKey();
                    Integer value = entry.getValue();
                    emojis = emojis+" " +EmojiList.emojis_with_index.get(value);
                    count++;
                    if(count>=5){
                        break;
                    }
//                    Log.i("  "+Double.toString(key)+" ==> ",Integer.toString(value));
                }
                result.setText(emojis);



            }
        });

    }



    private static MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        try (AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    public float[][] classify(float[][] input){
        try {
            ByteBuffer buffer = loadModelFile(this.getAssets());
            tflite = new Interpreter(buffer);
            Log.v("model", "TFLite model loaded.");
        } catch (IOException ex) {
            Log.e("error", ex.getMessage());
        }


        // Run inference.
        Log.v("message", "Classifying text with TF Lite...");
        float[][] output = new float[2][65];
        tflite.run(input, output);
        return output;
    }



}
