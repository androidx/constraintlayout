/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.constraintLayout.desktop.scan;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

/**
 * Provides Syntax Highlight
 */
public class SyntaxHighlight {
    JTextPane mEditor;
    Color sectionColor = new Color(0x5099D4);
    Color keywordColor = new Color(0x903890);
    Color attributeColor = new Color(0x384790);
    Color numberColor = new Color(0x0077e5);
    Color textColor = new Color(0x007726);
    Color opposingBracketColor = new Color(0xE08A63);
    Timer timer = new Timer(20, (a) -> syntaxHighlight());
    public boolean update = false;

    public SyntaxHighlight(JTextPane editor) {
        timer.setRepeats(false);
        mEditor = editor;
        mEditor.getStyledDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                if (update) {
                    return;
                }
                timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                if (update) {
                    return;
                }
                timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                if (update) {
                    return;
                }
                timer.restart();
            }
        });

    }

    boolean inopposingBracketColor = false;
    int currentHhighlight = -1;

    public void opposingBracketColor(String str, int cur, int len) {
        if (inopposingBracketColor) {
            return;
        }
        inopposingBracketColor = true;
        StyleContext sc = StyleContext.getDefaultStyleContext();
        if (currentHhighlight != -1) {
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
            highlight(mEditor, currentHhighlight, 1, aset);
            currentHhighlight = -1;
        }
        if (len >= cur && cur != 0) {
            char c = str.charAt(cur - 1);
            int pos = -1;
            switch (c) {
                case '{':
                    pos = findMatching(str, len, cur, '{', '}', true);
                    break;
                case '}':
                    pos = findMatching(str, len, cur, '{', '}', false);
                    break;
                case '[':
                    pos = findMatching(str, len, cur, '[', ']', true);
                    break;
                case ']':
                    pos = findMatching(str, len, cur, '[', ']', false);
                    break;
            }

            if (pos != -1) {
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, opposingBracketColor);
                highlight(mEditor, pos, 1, aset);
                currentHhighlight = pos;
            }
        }
        mEditor.setCaretPosition(cur);
        inopposingBracketColor = false;
    }

    int findMatching(String str, int len, int loc, char c, char rev_c, boolean forward) {
        int count = 1;
        if (forward) {
            for (int i = loc; i < len; i++) {
                if (str.charAt(i) == c) {
                    count++;
                } else if (str.charAt(i) == rev_c) {
                    count--;
                }
                if (count == 0) {
                    return i;
                }
            }
        } else {
            for (int i = loc - 2; i >= 0; i--) {
                if (str.charAt(i) == rev_c) {
                    count++;
                } else if (str.charAt(i) == c) {
                    count--;
                }
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }


    private void syntaxHighlight() {
        inopposingBracketColor = true;
        try {
            int cur = mEditor.getCaretPosition();
            mEditor.setSelectedTextColor(Color.RED);
            StyleContext sc = StyleContext.getDefaultStyleContext();
            String str = mEditor.getText();
            int len = str.length();
            if (len == 0) {
                return;
            }

            CLScan.parse(str, new CLScan.Scan() {
                @Override
                public void object(String type, int keyword, int offset, int length) {
                    AttributeSet aset;
                    switch (type) {
                        case "CLNumber":
                            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, numberColor);
                            highlight(mEditor, offset, length, aset);
                            break;
                        case "CLString":
                            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, textColor);
                            highlight(mEditor, offset, length, aset);
                            break;
                        default:
                            switch (keyword) {
                                case SECTION_ELEMENT:
                                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, sectionColor);
                                    break;
                                case ATTRIBUTE_ELEMENT:
                                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, keywordColor);
                                    break;
                                default:
                                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, attributeColor);

                            }
                            highlight(mEditor, offset, length, aset);
                            break;
                    }
                }
            });

            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
            mEditor.setCharacterAttributes(aset, false);
            mEditor.setCaretPosition(cur);
            inopposingBracketColor = false;
            opposingBracketColor(str, cur, len);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void highlight(JTextPane jp, int start, int len, AttributeSet aset) {
        update = true;
        jp.select(start, start + len);
        String msg = jp.getSelectedText();
        jp.replaceSelection("");
        jp.setCaretPosition(start);
        jp.setCharacterAttributes(aset, true);
        jp.replaceSelection(msg);
        update = false;
    }

}
