
package com.putatoe.putatoeconstructionserviceprovider.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.putatoe.putatoeconstructionserviceprovider.R;


public class CalculatorFragment extends Fragment {


    double in1 = 0, i2 = 0;
    TextView edittext1;
    boolean Add, Sub, Multiply, Divide, Remainder, deci;
    Button button_0, button_1, button_2, button_3, button_4, button_5, button_6, button_7, button_8, button_9, button_Add, button_Sub,
            button_Mul, button_Div, button_Equ, button_Del, button_Dot, button_Remainder;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_calculator, container, false);


        button_0 = view. findViewById(R.id.b0);
        button_1 = view.findViewById(R.id.b1);
        button_2 = view.findViewById(R.id.b2);
        button_3 = view.findViewById(R.id.b3);
        button_4 = view.findViewById(R.id.b4);
        button_5 = view. findViewById(R.id.b5);
        button_6 = view.findViewById(R.id.b6);
        button_7 = view.findViewById(R.id.b7);
        button_8 = view.findViewById(R.id.b8);
        button_9 = view.findViewById(R.id.b9);
        button_Dot = view.findViewById(R.id.bDot);
        button_Add = view.findViewById(R.id.badd);
        button_Sub = view.findViewById(R.id.bsub);
        button_Mul = view. findViewById(R.id.bmul);
        button_Div = view. findViewById(R.id.biv);
        button_Remainder = view. findViewById(R.id.BRemain);
        button_Del = view.findViewById(R.id.buttonDel);
        button_Equ = view. findViewById(R.id.buttoneql);

        edittext1 = view.findViewById(R.id.display);

        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "1");
            }
        });

        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "2");
            }
        });

        button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "3");
            }
        });

        button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "4");
            }
        });

        button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "5");
            }
        });

        button_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "6");
            }
        });

        button_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "7");
            }
        });

        button_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "8");
            }
        });

        button_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "9");
            }
        });

        button_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "0");
            }
        });

      button_Add.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(!TextUtils.isEmpty(edittext1.getText().toString()))
              {
                  char[] charArray = edittext1.getText().toString().toCharArray();
                  if(charArray[charArray.length-1] == '+' || charArray[charArray.length-1] == '-' || charArray[charArray.length-1]=='*'
                     || charArray[charArray.length-1] == '/')
                  {
                      charArray[charArray.length-1]='+';
                      String updatedString = "";
                      for(Character character : charArray)
                      {
                          updatedString+=character;
                      }
                      edittext1.setText(updatedString);
                  }
                  else
                  {
                      edittext1.append("+");
                  }

              }
          }
      });

      button_Sub.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(!TextUtils.isEmpty(edittext1.getText().toString()))
              {
                  char[] charArray = edittext1.getText().toString().toCharArray();
                  if(charArray[charArray.length-1] == '+' || charArray[charArray.length-1] == '-' || charArray[charArray.length-1]=='*'
                          || charArray[charArray.length-1] == '/')
                  {
                      charArray[charArray.length-1]='-';
                      String updatedString = "";
                      for(Character character : charArray)
                      {
                          updatedString+=character;
                      }
                      edittext1.setText(updatedString);
                  }
                  else
                  {
                      edittext1.append("-");
                  }
              }

          }
      });

      button_Del.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              edittext1.setText("");
          }
      });

      button_Div.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(!TextUtils.isEmpty(edittext1.getText().toString()))
              {
                  if(!TextUtils.isEmpty(edittext1.getText().toString()))
                  {
                      char[] charArray = edittext1.getText().toString().toCharArray();
                      if(charArray[charArray.length-1] == '+' || charArray[charArray.length-1] == '-' || charArray[charArray.length-1]=='*'
                              || charArray[charArray.length-1] == '/')
                      {
                          charArray[charArray.length-1]='/';
                          String updatedString = "";
                          for(Character character : charArray)
                          {
                              updatedString+=character;
                          }
                          edittext1.setText(updatedString);
                      }
                      else
                      {
                          edittext1.append("/");
                      }
                  }
              }
          }
      });

      button_Mul.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

                  if(!TextUtils.isEmpty(edittext1.getText().toString()))
                  {
                      char[] charArray = edittext1.getText().toString().toCharArray();
                      if(charArray[charArray.length-1] == '+' || charArray[charArray.length-1] == '-' || charArray[charArray.length-1]=='*'
                              || charArray[charArray.length-1] == '/')
                      {
                          charArray[charArray.length-1]='*';
                          String updatedString = "";
                          for(Character character : charArray)
                          {
                              updatedString+=character;
                          }
                          edittext1.setText(updatedString);
                      }
                      else
                      {
                          edittext1.append("*");
                      }
                  }
              }

      });


      button_Dot.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(TextUtils.isEmpty(edittext1.getText().toString()))
              {
                   edittext1.setText("0.");
              }
              else
              {
                  edittext1.append(".");
              }

          }
      });


      button_Remainder.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {


              if(TextUtils.isEmpty(edittext1.getText().toString()))
              {
                  Toast.makeText(getContext(), "Please enter the value", Toast.LENGTH_SHORT).show();
              }
              else
              {

                  ScriptEngineManager mgr = new ScriptEngineManager();
                  ScriptEngine engine = mgr.getEngineByName("rhino");
                  String foo = edittext1.getText().toString();


                  String signString = foo.replaceAll("[0-9.]","");
                  if(signString.length() == 1)
                  {
                      try {
                          edittext1.setText(""+engine.eval(foo));
                      } catch (ScriptException e) {
                          e.printStackTrace();
                      }
                      String answer = String.valueOf( Float.parseFloat(edittext1.getText().toString())/100);
                      edittext1.setText(answer);
                      return;
                  }


                  char[] charArray = foo.toCharArray();

                  String cal_string = "";

                  int count=0;
                  double total=0;
                  for(Character character : charArray)
                  {
                      if(character.equals('+') || character.equals('-') || character.equals('*') || character.equals('/') || character.equals('\0'))
                      {
                          count++;
                      }
                      if(count==2)
                      {

                          try {

                              total= (double) engine.eval(cal_string);

                              cal_string=String.valueOf(total);
                              total=0;
                              count=1;
                          } catch (ScriptException e) {
                              e.printStackTrace();
                          }
                      }
                      cal_string+=character;






                  }


                  try {

                      edittext1.setText(""+engine.eval(cal_string));
                      String answer = String.valueOf( Float.parseFloat(edittext1.getText().toString())/100);
                      edittext1.setText(answer);


//
                  } catch (ScriptException e) {

                      e.printStackTrace();
                  }





              }







          }
      });


      button_Equ.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

             List<Character> characterList = new ArrayList<>();

              if(TextUtils.isEmpty(edittext1.getText().toString()))
              {
                  Toast.makeText(getContext(), "Please enter the value", Toast.LENGTH_SHORT).show();
              }
              else
              {

                  ScriptEngineManager mgr = new ScriptEngineManager();
                  ScriptEngine engine = mgr.getEngineByName("rhino");
                  String foo = edittext1.getText().toString();


                  String signString = foo.replaceAll("[0-9.]","");
                  if(signString.length() == 1)
                  {
                      try {
                          edittext1.setText(""+engine.eval(foo));
                      } catch (ScriptException e) {
                          e.printStackTrace();
                      }
                      return;
                  }


                  char[] charArray = foo.toCharArray();

                  String cal_string = "";

                  int count=0;
                  double total=0;
                  for(Character character : charArray)
                  {
                      if(character.equals('+') || character.equals('-') || character.equals('*') || character.equals('/') || character.equals('\0'))
                      {
                          count++;
                      }
                      if(count==2)
                      {

                          try {
                              total= (double) engine.eval(cal_string);

                              cal_string=String.valueOf(total);
                              total=0;
                              count=1;
                          } catch (ScriptException e) {
                              e.printStackTrace();
                          }
                      }
                      cal_string+=character;






                  }


                  try {
                      edittext1.setText(""+engine.eval(cal_string));
                  } catch (ScriptException e) {
                      e.printStackTrace();
                  }








              }
          }
      });




        return view;



    }





}