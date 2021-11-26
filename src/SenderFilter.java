import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import org.jetbrains.kotlin.psi.*;

import java.util.List;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class SenderFilter implements Filter {
    public String TAG  = "SenderFilter = ";
    public final String eventClass;

    public SenderFilter(String eventClass) {
        this.eventClass = eventClass;
        Log.d(TAG +"1"+ eventClass + "/");
    }

    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (element == null) return false;
        if (PsiUtils.isKotlin(element)) {
            Log.d(TAG+"2" + element.getClass());
            if (element instanceof KtNameReferenceExpression) {
                KtNameReferenceExpression aa = (KtNameReferenceExpression) element;
                Log.d(TAG +"3"+ aa.getParent().getClass());
                if (aa.getParent() instanceof KtCallExpression) {
                    KtCallExpression aa1 = (KtCallExpression) aa.getParent();
                    if (aa1.getParent() instanceof KtDotQualifiedExpression) {
                        KtDotQualifiedExpression aa2 = (KtDotQualifiedExpression) aa1.getParent();
                        Log.d(TAG+"4" + aa2.getText() + "/");
                        boolean check = check(aa2);
                        if (check){
                            return true;
                        }

//                        text(aa2);
                    }
                }

            }
        } else {
            if (element instanceof PsiReferenceExpression) {
                if ((element = element.getParent()) instanceof PsiMethodCallExpression) {
                    PsiMethodCallExpression callExpression = (PsiMethodCallExpression) element;
                    PsiType[] types = callExpression.getArgumentList().getExpressionTypes();
                    for (PsiType type : types) {
                        PsiClass aClass = PsiUtils.getClass(type, element);
                        if (aClass != null && aClass.getName() != null && aClass.getName().equals(eventClass)) {
                            // pattern : EventBus.getDefault().post(new Event());
                            return true;
                        }
                    }
                    if ((element = element.getParent()) instanceof PsiExpressionStatement) {
                        if ((element = element.getParent()) instanceof PsiCodeBlock) {
                            PsiCodeBlock codeBlock = (PsiCodeBlock) element;
                            PsiStatement[] statements = codeBlock.getStatements();
                            for (PsiStatement statement : statements) {
                                if (statement instanceof PsiDeclarationStatement) {
                                    PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) statement;
                                    PsiElement[] elements = declarationStatement.getDeclaredElements();
                                    for (PsiElement variable : elements) {
                                        if (variable instanceof PsiLocalVariable) {
                                            PsiLocalVariable localVariable = (PsiLocalVariable) variable;
                                            PsiClass psiClass = PsiUtils.getClass(localVariable.getTypeElement().getType(), element);
                                            if (psiClass != null && psiClass.getName() != null && psiClass.getName().equals(eventClass)) {
                                                // pattern :
                                                //   Event event = new Event();
                                                //   EventBus.getDefault().post(event);
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        return false;
    }

    private void text(KtDotQualifiedExpression aa2) {
        PsiElement[] children = aa2.getChildren();
        for (int i = 0; i < children.length; i++) {
            Log.d("SenderFilter2 = " + children[i].getClass() + "/");
            Log.d("SenderFilter3 = " + children[i].getText() + "/");
            if (children[i] instanceof KtCallExpression) {
                KtCallExpression aa3 = (KtCallExpression) children[i];
                PsiElement[] children1 = aa3.getChildren();
                for (int j = 0; j < children1.length; j++) {
                    Log.d("SenderFilterj1 = " + children1[j].getClass() + "/");
                    Log.d("SenderFilterj2 = " + children1[j].getText() + "/");
                    if (children1[j] instanceof KtValueArgumentList) {
                        KtValueArgumentList aa4 = (KtValueArgumentList) children1[j];
                        List<KtValueArgument> arguments = aa4.getArguments();
                        for (int k = 0; k < arguments.size(); k++) {
                            KtValueArgument ktValueArgument = arguments.get(k);
                            Log.d("SenderFilterk1 = " + ktValueArgument.getText() + "/");
                            KtExpression argumentExpression = ktValueArgument.getArgumentExpression();
                            if (argumentExpression != null) {
                                Log.d("SenderFilterargumentExpression = " + argumentExpression.getText() + "/" + argumentExpression.getName());
                                PsiElement firstChild = argumentExpression.getFirstChild();
                                if (firstChild instanceof KtNameReferenceExpression) {
                                    Log.d("SenderFiltername = " + firstChild.getText() + "/");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean check(KtDotQualifiedExpression aa2) {
        PsiElement firstChild1 = aa2.getFirstChild();
        Log.d(TAG+"5" + firstChild1.getText() + "/"+firstChild1.getClass());
        if (firstChild1 instanceof KtDotQualifiedExpression){
            //适配CmpDispatcher.getInstance()
            if (firstChild1.getText().equals("CmpDispatcher.getInstance()")){
                if (cmp(aa2)) return true;
            }
        }
        if (firstChild1 instanceof KtNameReferenceExpression) {
            Log.d(TAG+"6" + firstChild1.getText() + "/");
            //适配NotifyDispatcher
            if (firstChild1.getText().equals("NotifyDispatcher")) {
                PsiElement lastChild = aa2.getLastChild();
                if (lastChild instanceof KtCallExpression) {
                    KtCallExpression aa3 = (KtCallExpression) lastChild;
                    PsiElement lastChild1 = aa3.getLastChild();
                    if (lastChild1 instanceof KtValueArgumentList) {
                        KtValueArgumentList aa4 = (KtValueArgumentList) lastChild1;
                        List<KtValueArgument> arguments = aa4.getArguments();
                        for (int k = 0; k < arguments.size(); k++) {
                            KtValueArgument ktValueArgument = arguments.get(k);
                            Log.d(TAG+"7" + ktValueArgument.getText() + "/");
                            KtExpression argumentExpression = ktValueArgument.getArgumentExpression();
                            if (argumentExpression != null) {
                                Log.d(TAG+"8" + argumentExpression.getText() + "/" + argumentExpression.getName());
                                PsiElement firstChild = argumentExpression.getFirstChild();
                                if (firstChild instanceof KtNameReferenceExpression) {
                                    Log.d(TAG+"9" + firstChild.getText() + "/");
                                    if (firstChild.getText().equals(eventClass)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //适配 dispatcher.sendCall
                if (cmp(aa2)) return true;
            }
        }
        return false;
    }

    private boolean cmp(KtDotQualifiedExpression aa2) {
        PsiElement lastChild = aa2.getLastChild();
        if (lastChild instanceof KtCallExpression) {
            KtCallExpression aa3 = (KtCallExpression) lastChild;
            PsiElement lastChild1 = aa3.getLastChild();
            PsiElement firstChild2 = aa3.getFirstChild();
            if (firstChild2 instanceof KtNameReferenceExpression) {
                String text = firstChild2.getText();
                Log.d(TAG+"10" + text + "/");
                if (text.equals("sendEvent")||text.equals("sendCall")) {
                    if (lastChild1 instanceof KtValueArgumentList) {
                        KtValueArgumentList aa4 = (KtValueArgumentList) lastChild1;
                        List<KtValueArgument> arguments = aa4.getArguments();
                        for (int k = 0; k < arguments.size(); k++) {
                            KtValueArgument ktValueArgument = arguments.get(k);
                            Log.d(TAG+"11" + ktValueArgument.getText() + "/");
                            KtExpression argumentExpression = ktValueArgument.getArgumentExpression();
                            if (argumentExpression != null) {
                                Log.d(TAG+"12" + argumentExpression.getText() + "/" + argumentExpression.getName());
                                PsiElement firstChild = argumentExpression.getFirstChild();
                                if (firstChild instanceof KtNameReferenceExpression) {
                                    //
                                    Log.d(TAG+"13" + firstChild.getText() + "/");
                                    if (firstChild.getText().equals(eventClass)) {
                                        return true;
                                    }
                                }else {
                                    //适配 val aa =AddClearScreenCall(entryView)
                                    //     CmpDispatcher.getInstance().sendCall(aa)
                                    if (argumentExpression.getParent() instanceof KtValueArgument){
                                        if (argumentExpression.getParent().getParent() instanceof KtValueArgumentList ){
                                            if (argumentExpression.getParent().getParent().getParent() instanceof KtCallExpression){
                                                if (argumentExpression.getParent().getParent().getParent().getParent() instanceof KtDotQualifiedExpression){
                                                    if (argumentExpression.getParent().getParent().getParent().getParent().getParent() instanceof KtBlockExpression){
                                                        KtBlockExpression parent = (KtBlockExpression) argumentExpression.getParent().getParent().getParent().getParent().getParent();
                                                        PsiElement[] children = parent.getChildren();
                                                        for (int i = 0; i < children.length; i++) {
                                                            if (children[i] instanceof KtProperty){
                                                                Log.d(TAG+"14" + children[i].getText() + "/");
                                                                PsiElement[] children1 = children[i].getChildren();
                                                                for (int j = 0; j < children1.length; j++) {
                                                                    Log.d(TAG+"15" + children1[j].getClass() + "/");
                                                                    Log.d(TAG+"16" + children1[j].getText() + "/");
                                                                    if (children1[j] instanceof KtCallExpression){
                                                                        PsiElement firstChild1 = children1[j].getFirstChild();
                                                                        String text1 = firstChild1.getText();
                                                                        Log.d(TAG+"17" + text1 + "/");
                                                                        if (text1.equals(eventClass)){
                                                                            return true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return false;
    }
}
