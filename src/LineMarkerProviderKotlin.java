import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.idea.refactoring.memberInfo.KtPsiClassWrapper;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.psi.psiUtil.KtPsiUtilKt;
import org.jetbrains.uast.kotlin.KotlinUClass;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * Created by by likfe ( https://github.com/likfe/ )  on 18/03/05.
 */
public class LineMarkerProviderKotlin implements com.intellij.codeInsight.daemon.LineMarkerProvider {
    public static final Icon ICON = IconLoader.getIcon("/icons/icon.png");
    public static final int MAX_USAGES = 100;


    private static GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    System.out.println("kt SHOW_RECEIVERS 0: " + psiElement.getText());


                    if (psiElement instanceof KtDotQualifiedExpression) {
                        System.out.println("kt SHOW_RECEIVERS 1: " + psiElement);

                        try {
                            KtDotQualifiedExpression expression = (KtDotQualifiedExpression) psiElement;
                            KtCallExpression callExpression = (KtCallExpression) expression.getLastChild();
                            System.out.println("kt SHOW_RECEIVERS 2: " + callExpression);
                            KtValueArgumentList argumentList = callExpression.getValueArgumentList();
                            System.out.println("kt SHOW_RECEIVERS 3: " + argumentList);
                            KtValueArgument argument = argumentList.getArguments().get(0);
                            KtCallExpression referenceExpression = (KtCallExpression) argument.getFirstChild();
                            System.out.println("kt SHOW_RECEIVERS 4: " + referenceExpression);
                            KtNameReferenceExpression leafPsiElement = (KtNameReferenceExpression) referenceExpression.getFirstChild();
                            System.out.println("kt SHOW_RECEIVERS 5: " + leafPsiElement);

                            //KtPsiClassWrapper psiClassWrapper= KotlinJavaPsiFacade.getInstance(psiElement.getProject()).findClass(leafPsiElement.getClass(),)

                            System.out.println("kt SHOW_RECEIVERS 6: " + argument.getText());
                            PsiClass ktClass = (PsiClass) new KtClass(leafPsiElement.getNode());
                            System.out.println("kt SHOW_RECEIVERS 7: " + ktClass.getText());
                            if (ktClass!=null){
                                new ShowUsagesAction1(new ReceiverFilter()).startFindUsages( ktClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                            }


                        } catch (Exception | Error throwable) {
                            System.out.println("dadadada"+throwable.getMessage());
                            throwable.fillInStackTrace();
                        }


//                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
//                        if (expressionTypes.length > 0) {
//                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0]);
//                            if (eventClass != null) {
//                                new ShowUsagesAction(new ReceiverFilterKotlin())
//                                        .startFindUsages(
//                                                eventClass, new RelativePoint(e),
//                                                PsiUtilBase.findEditor(psiElement),
//                                                Constants.MAX_USAGES);
//                            }
//                        }
                    }
                }
            };

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo( PsiElement psiElement) {

        if (!PsiUtils.checkIsKotlinInstalled()) return null;
        if (!PsiUtils.isKotlin(psiElement)) return null;
//        aa(psiElement);
        if (PsiUtils.isEventBusPost(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        }


        return null;
    }

    private void aa(PsiElement psiElement) {
        System.out.println(psiElement.getClass());

        if (psiElement instanceof KtCallExpression){
            KtCallExpression a  = (KtCallExpression) psiElement;
            PsiElement firstChild = a.getFirstChild();
            if (firstChild instanceof KtNameReferenceExpression){
                KtNameReferenceExpression referenceExpression = (KtNameReferenceExpression) firstChild;
                System.out.println("KtNameReferenceExpression = "+referenceExpression.getReferencedName()+"/");


                LeafPsiElement callExpression = (LeafPsiElement) firstChild.getLastChild();
                System.out.println("KtNameReferenceExpression1 = "+firstChild.getLastChild().getClass());
            }

        }

//        if (psiElement instanceof KtDotQualifiedExpression) {
//            KtDotQualifiedExpression all = (KtDotQualifiedExpression) psiElement;
//            if (all.getFirstChild() instanceof KtDotQualifiedExpression) {
//                String start = all.getFirstChild().getText();
//                System.out.println("KtNameReferenceExpression1 ="+start);
//            }
//        }
//
//        if (psiElement instanceof KtFunctionLiteral){
//            KtFunctionLiteral fun  = (KtFunctionLiteral) psiElement;
//            System.out.println("fun ="+fun.getName()+"/"+fun.getFqName()+fun.toString());
//        }
//
//        if (psiElement instanceof KtNamedFunction){
//            KtNamedFunction fun  = (KtNamedFunction) psiElement;
//            System.out.println("KtNamedFunction ="+fun.getName()+"/"+fun.getName()+fun.getAnnotations());
//        }


        //KtPsiClassWrapper psiClassWrapper= KotlinJavaPsiFacade.getInstance(psiElement.getProject()).findClass(leafPsiElement.getClass(),)


    }


    @Override
    public void collectSlowLineMarkers( List<? extends PsiElement> list,  Collection<? super LineMarkerInfo<?>> collection) {
    }
}
