package androidx.constraintLayout.desktop.scan;

import androidx.constraintlayout.core.parser.*;

import javax.swing.tree.DefaultMutableTreeNode;

public class CLTreeNode  extends DefaultMutableTreeNode {
    CLElement mObject;
    String mName;
    public int mKeyStart = -1;
    public int mKeyEnd = -1;
    public static DefaultMutableTreeNode parse(String str,DefaultMutableTreeNode root) throws CLParsingException {
        CLObject obj = CLParser.parse(str);
        int n = obj.size();
        for (int i = 0; i < n; i++) {
            CLElement tmp = obj.get(i);
            if (tmp instanceof CLKey) {
                root.add( new CLTreeNode((CLKey)tmp));

            }
        }
        return root;
    }


    CLTreeNode(String root) throws CLParsingException {
        super(root);
    }

    CLTreeNode(CLKey clkey)  throws CLParsingException {
        super(clkey.content());
        mObject = clkey;
        mName = clkey.content();
        mKeyStart =(int) clkey.getStart();
        mKeyEnd =(int) clkey.getEnd();
        CLElement value = clkey.getValue();
        if (value != null) {
            if (value instanceof CLArray) {
                CLArray array = (CLArray) value;
                int count = array.size();
                for (int j = 0; j < count; j++) {
                    CLElement v = array.get(j);
                    if (v instanceof CLObject) {
                        CLObject obj = (CLObject)v;
                        int n = obj.size();
                        for (int i = 0; i < n; i++) {
                            CLElement tmp = obj.get(i);
                            if (!(tmp instanceof CLKey)) {
                                continue;
                            }

                            add(new CLTreeNode( ((CLKey) tmp)));
                        }
                    }
                }
            }else if (value instanceof CLObject) {
                CLObject obj = (CLObject)value;
                int n = obj.size();
                for (int i = 0; i < n; i++) {
                    CLElement tmp = obj.get(i);
                    if (!(tmp instanceof CLKey)) {
                        continue;
                    }

                    add(new CLTreeNode( ((CLKey) tmp)));
                }
            }

        }
    }




}
