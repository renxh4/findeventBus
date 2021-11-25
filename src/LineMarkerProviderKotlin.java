import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.psi.stubs.elements.KtClassElementType;
import org.jetbrains.kotlin.resolve.lazy.data.KtClassInfo;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by by likfe ( https://github.com/likfe/ )  on 18/03/05.
 */
public class LineMarkerProviderKotlin implements com.intellij.codeInsight.daemon.LineMarkerProvider {
    public static final Icon ICON = IconLoader.getIcon("/icons/icon.png");
    public static final int MAX_USAGES = 100;
    private static GutterIconNavigationHandler<PsiElement> SHOW_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    System.out.println("SHOW_SENDERS_nitiy1=" + psiElement.getClass() + "/" + psiElement.getText());
                    if (psiElement instanceof KtNamedFunction) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass("com.immomo.molive.foundation.eventcenter.eventdispatcher.NotifyDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod = eventBusClass.findMethodsByName("dispatch", false)[0];
                        KtNamedFunction namedFunction = (KtNamedFunction) psiElement;
                        String name = null;
                        name = getName(namedFunction, name);
                        new ShowUsagesAction(new SenderFilter(name)).startFindUsages(null, postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                    }
                }
            };

    private static String getName(KtNamedFunction namedFunction, String name) {
        PsiElement[] children = namedFunction.getChildren();
        for (int i = 0; i < children.length; i++) {
            System.out.println("SHOW_SENDERS_nitiy2=" + children[i].getClass());
            System.out.println("SHOW_SENDERS_nitiy3=" + children[i].getText());
            if (children[i] instanceof KtParameterList){
                KtParameterList ktParameterList= (KtParameterList) children[i];
                PsiElement[] children1 = ktParameterList.getChildren();
                for (int j = 0; j <children1.length ; j++) {
                    System.out.println("SHOW_SENDERS_nitiy4=" + children1[j].getClass());
                    System.out.println("SHOW_SENDERS_nitiy5=" + children1[j].getText());
                    if (children1[j] instanceof KtParameter){
                        PsiElement[] children2 = children1[j].getChildren();
                        for (int k = 0; k < children2.length; k++) {
                            System.out.println("SHOW_SENDERS_nitiy6"+children2[k].getClass());
                            System.out.println("SHOW_SENDERS_nitiy7"+children2[k].getText());
                            if (children2[k] instanceof KtTypeReference){
                                KtTypeReference   bb = (KtTypeReference) children2[k];
                                name = bb.getText();
                                if (name.contains("?")){
                                    KtTypeElement typeElement = bb.getTypeElement();
                                    PsiElement[] children3 = typeElement.getChildren();
                                    for (int l = 0; l < children3.length; l++) {
                                        System.out.println("SHOW_SENDERS_nitiy9"+children3[l].getClass());
                                        System.out.println("SHOW_SENDERS_nitiy10"+children3[l].getText());
                                        if (children3[l] instanceof KtUserType){
                                            name = children3[l].getText();
                                        }
                                    }
                                }

                                System.out.println("SHOW_SENDERS_nitiy8"+ name);
                            }
                        }
                    }

                }
            }
        }
        return name;
    }



    private static GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    aa(psiElement);
                    System.out.println("ktSHOW_RECEIVERS0: " + psiElement.getText() + psiElement.getClass());
                    if (psiElement instanceof KtCallExpression) {
                        System.out.println("ktSHOW_RECEIVERS1: " + psiElement);
                        PsiElement[] children = psiElement.getChildren();
                        for (int i = 0; i < children.length; i++) {
                            System.out.println("ktSHOW_RECEIVERS2: " + children[i].getClass());
                            System.out.println("ktSHOW_RECEIVERS3: " + children[i].getText());
                            if (children[i] instanceof KtValueArgumentList) {
                                KtValueArgumentList list = (KtValueArgumentList) children[i];
                                List<KtValueArgument> arguments = list.getArguments();
                                for (int k = 0; k < arguments.size(); k++) {
                                    KtValueArgument ktValueArgument = arguments.get(k);
                                    System.out.println("ktSHOW_RECEIVERS4:" + ktValueArgument.getText() + "/");
                                    KtExpression argumentExpression = ktValueArgument.getArgumentExpression();
                                    if (argumentExpression != null) {
                                        System.out.println("ktSHOW_RECEIVERS5:" + argumentExpression.getText() + "/" + argumentExpression.getName());
                                        PsiElement firstChild = argumentExpression.getFirstChild();
                                        if (firstChild instanceof KtNameReferenceExpression) {
                                            System.out.println("ktSHOW_RECEIVERS6:" + firstChild.getText() + "/");
                                            KtNameReferenceExpression aa = (KtNameReferenceExpression) firstChild;
                                            KtClass ktClass = new KtClass(aa.getNode());
                                            new ShowUsagesAction(new ReceiverFilter()).startFindUsages(null, ktClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            };

    private static void aa(PsiElement psiElement) {
        Project project = psiElement.getProject();
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        String basePath = project.getBasePath();
        String projectFilePath = project.getProjectFilePath();
        VirtualFile projectFile = project.getProjectFile();
        VirtualFile[] children = projectFile.getChildren();
        for (int i = 0; i < children.length; i++) {
            System.out.println("allfile:base" + children[i].getName());
        }

        System.out.println("allfile:base1" + projectFilePath);
        PsiJavaParserFacade parserFacade = javaPsiFacade.getParserFacade();

    }


    private static GutterIconNavigationHandler<PsiElement> SHOW_CmpSafeDispatcher_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof KtNamedFunction) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass("com.immomo.molive.common.component.common.dispatcher.CmpSafeDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod = eventBusClass.findMethodsByName("sendEvent", false)[0];
                        PsiMethod postMethod1 = eventBusClass.findMethodsByName("sendCall", false)[0];

                        PsiClass eventBusClass1 = javaPsiFacade.findClass("com.immomo.molive.common.component.common.dispatcher.CmpDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod2 = eventBusClass1.findMethodsByName("sendEvent", false)[0];
                        PsiMethod postMethod3 = eventBusClass1.findMethodsByName("sendCall", false)[0];
                        ArrayList<PsiElement> psiMethodArrayList = new ArrayList<>();
                        psiMethodArrayList.add(postMethod);
                        psiMethodArrayList.add(postMethod1);
                        psiMethodArrayList.add(postMethod2);
                        psiMethodArrayList.add(postMethod3);
                        System.out.println("发送方法" + postMethod);
                        KtNamedFunction ktNamedFunction = (KtNamedFunction) psiElement;
                        String name  = null;
                        name = getName(ktNamedFunction, name);
                        new ShowUsagesAction(new SenderFilter(name)).startFindUsages(psiMethodArrayList, postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                    }
                }
            };

    private static GutterIconNavigationHandler<PsiElement> SHOW_CmpSafeDispatcher_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
                        System.out.println("接收1" + expression);
                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                        if (expressionTypes.length > 0) {
                            System.out.println("接收2" + expressionTypes[0]);
                            System.out.println("接收3" + psiElement);
                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0], psiElement);
                            System.out.println("接收4" + eventClass);
                            if (eventClass != null) {
                                new ShowUsagesAction(new DispatchReceiverFilter(eventClass)).startFindUsages(null, eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                            }
                        }
                    }
                }
            };


    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(PsiElement psiElement) {

        if (!PsiUtils.checkIsKotlinInstalled()) return null;
        if (!PsiUtils.isKotlin(psiElement)) return null;
        if (PsiUtils.isEventBusPostKT(psiElement, "Postkt")) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_CmpSafeDispatcher_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiverKT(psiElement, "ReceiverKT")) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_CmpSafeDispatcher_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusPostKTNotifyDispatcher(psiElement, "NotifyDispatcherPostkt")) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiverKTNotifyDispatcher(psiElement, "ReceiverKT")) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        }
        return null;
    }


}
