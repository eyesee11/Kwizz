import React from 'react'

type ModalProps = {
  open: boolean
  title?: string
  onClose: () => void
  children: React.ReactNode
}

export default function Modal({ open, title, onClose, children }: ModalProps) {
  if (!open) return null
  return (
    <div style={{position:'fixed', inset:0 as any, background:'rgba(0,0,0,0.4)', display:'grid', placeItems:'center', zIndex:50}}>
      <div className="card brutal" style={{width:'min(920px, 96vw)', maxHeight:'88vh', overflow:'auto'}}>
        <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
          <h3 style={{marginTop:0}}>{title}</h3>
          <button className="btn brutal" onClick={onClose}>Close</button>
        </div>
        <div>
          {children}
        </div>
      </div>
    </div>
  )
}


