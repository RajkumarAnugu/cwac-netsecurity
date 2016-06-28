package com.commonsware.cwac.netseccfg.okhttp3.test.pub;

import android.support.test.InstrumentationRegistry;
import com.commonsware.cwac.netseccfg.CertificateNotMemorizedException;
import com.commonsware.cwac.netseccfg.MemorizingTrustManager;
import com.commonsware.cwac.netseccfg.TrustManagerBuilder;
import com.commonsware.cwac.netseccfg.okhttp3.test.R;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class MemoSpecificAuthorityMismatchTest extends SimpleHTTPSTest {
  private MemorizingTrustManager memo;
  private boolean onNotMemorizedCalled=false;

  @Before
  public void initMemo()
    throws CertificateException, NoSuchAlgorithmException,
    KeyStoreException, IOException {
    MemorizingTrustManager.Options opts=new MemorizingTrustManager.Options(
      InstrumentationRegistry.getContext(), "memo", "sekrit");

    memo=new MemorizingTrustManager(opts);
  }

  @After
  public void cleanupMemo()
    throws CertificateException, NoSuchAlgorithmException,
    KeyStoreException, IOException {
    memo.clear(true);
  }

  @Override
  public void testRequest() throws Exception {
    super.testRequest();

    /*
      onNotMemorized() should not be called, since we should have
      failed the Verisign-only test
     */
    Assert.assertFalse("onNotMemorized() called", onNotMemorizedCalled);
  }

  @Override
  protected TrustManagerBuilder getBuilder()
    throws KeyStoreException, NoSuchAlgorithmException {
    return(new TrustManagerBuilder()
      .withConfig(InstrumentationRegistry.getContext(), R.xml.verisign,
        false)
      .and()
      .add(memo));
  }

  @Override
  protected boolean isPositiveTest() {
    return(false);
  }

  @Override
  protected void onNotMemorized(CertificateNotMemorizedException e)
    throws Exception {
    if (!onNotMemorizedCalled) {
      onNotMemorizedCalled=true;
      memo.allowCertForProcess(e.getCertificateChain());
    }
    else {
      throw e;
    }
  }
}
