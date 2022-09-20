package androidx.demo.lua;

import android.util.Log;

import androidx.constraintlayout.core.parser.CLElement;
import androidx.constraintlayout.core.parser.CLKey;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParser;
import androidx.constraintlayout.core.parser.CLParsingException;

public class ExpressionEngine {
    private static final String TAG = "ExpEng";
    NativeLib nativeLib = new NativeLib();

    public String process(String str) {
        try {
            CLObject json = CLParser.parse(str);
            if (json.has("vars")) {
                Log.v(TAG, " >>>>>>>>>>> ");
                CLElement obj = json.get("vars");
                CLObject o = (CLObject) obj;
                int n = o.size();
                String calc = "";
                for (int i = 0; i < n; i++) {
                    CLKey key = (CLKey) o.get(i);

                    String name = key.getName();
                    String exp = key.getValue().content();

                    calc = calc + "\n" + name + " = " + exp + ";";

                    String ans = nativeLib.stringFromJNI(calc, name);
                     str = str.replaceAll("'#"+name+"'",ans);
                }
            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
