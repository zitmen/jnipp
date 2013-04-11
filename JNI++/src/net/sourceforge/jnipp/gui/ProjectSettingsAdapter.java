package net.sourceforge.jnipp.gui;

import net.sourceforge.jnipp.project.*;

public class ProjectSettingsAdapter {

    protected PeerGenSettings _peergenset = null;
    protected boolean _doPeerGen = false;
    protected boolean _peerIsDestructive = false;
    protected ProxyGenSettings _proxygenset = null;
    protected boolean _doProxyGen = false;
    protected boolean _proxyGenerateSetters = false;
    protected boolean _proxyGenerateGetters = false;
    protected boolean _proxyUseInheritance = false;
    protected boolean _proxyUseRichTypes = false;
    protected boolean _proxyInnerClasses = false;

    public ProjectSettingsAdapter() {
    }

    public void setPeerGenSettings(PeerGenSettings peergenset) {
        _peergenset = peergenset;
        initPeerGenSettings();
    }

    public PeerGenSettings getPeerGenSettings() {
        return (_peergenset);
    }

    public void setPeerGenSettingsPushTo(PeerGenSettings peergenset) throws NullPointerException {
        peergenset.setDestructive(_peerIsDestructive);
        _peergenset = peergenset;
    }

    public void setProxyGenSettings(ProxyGenSettings proxygenset) {
        _proxygenset = proxygenset;
        initProxyGenSettings();
    }

    public ProxyGenSettings getProxyGenSettings() {
        return (_proxygenset);
    }

    public void setProxyGenSettingsPushTo(ProxyGenSettings proxygenset) throws NullPointerException {
        proxygenset.setGenerateAttributeSetters(_proxyGenerateSetters);
        proxygenset.setGenerateAttributeGetters(_proxyGenerateGetters);
        proxygenset.setUseInheritance(_proxyUseInheritance);
        proxygenset.setUseRichTypes(_proxyUseRichTypes);
        proxygenset.setGenerateInnerClasses(_proxyUseRichTypes);
        _proxygenset = proxygenset;
    }

    protected void initPeerGenSettings() {
        if (_peergenset == null) {
            return;
        }

        _doPeerGen = true;
        _peerIsDestructive = _peergenset.isDestructive();
    }

    protected void initProxyGenSettings() {
        if (_proxygenset == null) {
            return;
        }

        _doProxyGen = true;
        _proxyGenerateSetters = _proxygenset.getGenerateAttributeSetters();
        _proxyGenerateGetters = _proxygenset.getGenerateAttributeGetters();
        _proxyUseInheritance = _proxygenset.getUseInheritance();
        _proxyUseRichTypes = _proxygenset.getUseRichTypes();
        _proxyInnerClasses = _proxygenset.getGenerateInnerClasses();
    }

    public boolean getPeerIsDirty() {
        if (_peergenset == null) {
            if (_doPeerGen == false) {
                //nothing to do.
                return (false);
            } else {
                //need to build one
                return (true);
            }
        } else {
            return (_peerIsDestructive != _peergenset.isDestructive());
        }
    }

    public boolean getProxyIsDirty() {
        if (_proxygenset == null) {
            if (_doProxyGen == false) {
                //nothing to do.
                return (false);
            } else {
                //need to build one
                return (true);
            }
        } else {
            return ((_proxyGenerateSetters != _proxygenset.getGenerateAttributeSetters())
                    || (_proxyGenerateGetters != _proxygenset.getGenerateAttributeGetters())
                    || (_proxyUseInheritance != _proxygenset.getUseInheritance())
                    || (_proxyUseRichTypes != _proxygenset.getUseRichTypes())
                    || (_proxyInnerClasses != _proxygenset.getGenerateInnerClasses()));
        }
    }

    public boolean getDoPeerGen() {
        return _doPeerGen;
    }

    public void setDoPeerGen(boolean doPeerGen) {
        _doPeerGen = doPeerGen;
    }

    public boolean getPeerIsDestructive() {
        return _peerIsDestructive;
    }

    public void setPeerIsDestructive(boolean isDestructive) {
        _peerIsDestructive = isDestructive;
    }

    public boolean getDoProxyGen() {
        return _doProxyGen;
    }

    public void setDoProxyGen(boolean doProxyGen) {
        _doProxyGen = doProxyGen;
    }

    public boolean getProxyGenerateGetters() {
        return _proxyGenerateGetters;
    }

    public void setProxyGenerateGetters(boolean generateGetters) {
        _proxyGenerateGetters = generateGetters;
    }

    public boolean getProxyGenerateSetters() {
        return _proxyGenerateSetters;
    }

    public void setProxyGenerateSetters(boolean generateSetters) {
        _proxyGenerateSetters = generateSetters;
    }

    public boolean getProxyUseInheritance() {
        return _proxyUseInheritance;
    }

    public void setProxyUseInheritance(boolean useInheritance) {
        _proxyUseInheritance = useInheritance;
    }

    public boolean getProxyGenerateInnerClasses() {
        return _proxyInnerClasses;
    }

    public void setProxyGenerateInnerClasses(boolean generateInnerClasses) {
        _proxyInnerClasses = generateInnerClasses;
    }

    public boolean getProxyUseRichTypes() {
        return _proxyUseRichTypes;
    }

    public void setProxyUseRichTypes(boolean useRichTypes) {
        _proxyUseRichTypes = useRichTypes;
    }
}
