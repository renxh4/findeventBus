import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class HaniEventBusLineMarkerProvider implements LineMarkerProvider {

    public static final Icon ICON = IconLoader.getIcon("/icons/icon.png");
    public static final Icon ICON_SUN = IconLoader.findIcon("/icons/icon_sun.png");

    public static final int MAX_USAGES = 100;

    private static GutterIconNavigationHandler<PsiElement> SHOW_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethod) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass("com.immomo.molive.foundation.eventcenter.eventdispatcher.NotifyDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod = eventBusClass.findMethodsByName("dispatch", false)[0];
                        PsiMethod method = (PsiMethod) psiElement;
                        PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();
                        new ShowUsagesAction(new SenderFilter(eventClass)).startFindUsages(postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                    }
                }
            };



    private static GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                        if (expressionTypes.length > 0) {
                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0], psiElement);
                            if (eventClass != null) {
                                new ShowUsagesAction(new ReceiverFilter()).startFindUsages(eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                            }
                        }
                    }
                }
            };


    private static GutterIconNavigationHandler<PsiElement> SHOW_CmpSafeDispatcher_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethod) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass("com.immomo.molive.common.component.common.dispatcher.CmpSafeDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod = eventBusClass.findMethodsByName("sendEvent", false)[0];
                        System.out.println("发送方法"+postMethod);
                        PsiMethod method = (PsiMethod) psiElement;
                        PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();
                        System.out.println("发送类"+eventClass);
                        new ShowUsagesAction(new SenderFilter(eventClass)).startFindUsages(postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                    }
                }
            };

    private static GutterIconNavigationHandler<PsiElement> SHOW_CmpSafeDispatcher_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
                        System.out.println("接收1"+expression);
                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                        if (expressionTypes.length > 0) {
                            System.out.println("接收2"+expressionTypes[0]);
                            System.out.println("接收3"+psiElement);
                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0], psiElement);
                            System.out.println("接收4"+eventClass);
                            if (eventClass != null) {
                                new ShowUsagesAction(new DispatchReceiverFilter()).startFindUsages(eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                            }
                        }
                    }
                }
            };


    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (PsiUtils.isEventBusPost(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                     null, SHOW_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiver(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                     null, SHOW_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        }else if (PsiUtils.isCmpSafeDispatcherPost(psiElement)){
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                     null, SHOW_CmpSafeDispatcher_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        }else if (PsiUtils.isCmpSafeDispatcherReceiver(psiElement)){
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                   null, SHOW_CmpSafeDispatcher_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        }

        return null;
    }

}
